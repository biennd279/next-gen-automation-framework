package org.zaproxy.addon.naf.pipeline

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.apache.commons.httpclient.URI
import org.parosproxy.paros.extension.history.ExtensionHistory
import org.parosproxy.paros.model.HistoryReference
import org.parosproxy.paros.model.SiteNode
import org.parosproxy.paros.network.HttpMessage
import org.parosproxy.paros.network.HttpSender
import org.zaproxy.addon.naf.NafPolicySupport
import org.zaproxy.addon.naf.model.*
import org.zaproxy.zap.authentication.FormBasedAuthenticationMethodType
import org.zaproxy.zap.authentication.UsernamePasswordAuthenticationCredentials
import org.zaproxy.zap.extension.ascan.ScanPolicy
import org.zaproxy.zap.extension.spider.ExtensionSpider
import org.zaproxy.zap.extension.users.ContextUserAuthManager
import org.zaproxy.zap.model.TechSet
import org.zaproxy.zap.users.User
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.swing.SwingUtilities
import kotlin.coroutines.CoroutineContext

class InitContextPipeline(
    val scanTemplate: ScanTemplate,
    val defaultPolicy: ScanPolicy,
    override val coroutineContext: CoroutineContext
): NafPipeline<NafScanContext>(NafPhase.INIT) {

    val extensionSpider by lazy {
        extensionLoader
            .getExtension(ExtensionSpider::class.java)
    }

    val extensionHistory by lazy {
        extensionLoader
            .getExtension(ExtensionHistory::class.java)
    }

    override suspend fun start(nafScanContext: NafScanContext): NafScanContext {

        val session = model.session
        val context = session.getContext("NAF") ?: session.getNewContext("NAF")
        val policy = defaultPolicy
        val nafPolicySupport = NafPolicySupport(policy, policy.defaultThreshold)
        val user: User?

        with(scanTemplate) {

            when (val method = authenticationOptions.method) {
                NafAuthenticationMethod.None -> {
                    user = null
                }
                is NafAuthenticationMethod.FormBased -> {
                    user = User(context.id, "Naf-${method.username}")
                    val userAuthManager = ContextUserAuthManager(context.id)

                    user.authenticationCredentials = UsernamePasswordAuthenticationCredentials(
                        method.username,
                        method.password
                    )

                    userAuthManager.addUser(user)

                    val formBasedMethodType = FormBasedAuthenticationMethodType()
                    val formBasedMethod = formBasedMethodType
                        .createAuthenticationMethod(context.id)

                    val loginSiteNode = getLoginNode(method.loginUrl)

                    loginSiteNode.let { it ->
                        val body = it.historyReference
                            .httpMessage
                            .requestBody
                            .bytes
                            .toString(Charsets.UTF_8)

                        val bodyMap = body
                            .split("&")
                            .associate { parameter ->
                                val (key, value) = parameter.split("=")

                                key to value
                            }

                        var authBody = body

                        val usernameField = method.loginField.first.urlEncode()
                        if (bodyMap.containsKey(usernameField)) {
                            authBody = authBody
                                .replace(
                                    "$usernameField=${bodyMap[usernameField]}",
                                    "$usernameField={%username%}"
                                )
                        }

                        val passwordField = method.loginField.second.urlEncode()

                        if (bodyMap.containsKey(passwordField)) {
                            authBody = authBody
                                .replace(
                                    "$passwordField=${bodyMap[passwordField]}",
                                    "$passwordField={%password%}"
                                )
                        }

                        kotlin.runCatching {
                            formBasedMethod.setAndReturnPrivateProperty("loginRequestURL", method.loginUrl)
                            formBasedMethod.setAndReturnPrivateProperty("loginRequestBody", authBody)
                        }.onFailure {
                            println(it)
                        }
                    }

                    kotlin.runCatching {
                        formBasedMethod.setAndReturnPrivateProperty("loginPageUrl", method.loginPage)
                    }.onFailure {
                        println(it)
                    }

                    formBasedMethod.setLoggedInIndicatorPattern(method.loginPattern)
                    formBasedMethod.setLoggedOutIndicatorPattern(method.logoutPattern)

                    context.authenticationMethod = formBasedMethod
                }
            }

            val nafPluginMap = scanOptions.plugins.associateBy { it.id }

            policy.pluginFactory!!
                .allPlugin
                .forEach { plugin ->
                    nafPluginMap[plugin.id]?.let { nafPlugin ->
                        plugin.alertThreshold = nafPlugin.threshold.toAlertThreshold()
                        plugin.attackStrength = nafPlugin.strength.toAttackStrength()

                        if (nafPlugin.threshold == NafPlugin.Threshold.OFF) {
                            nafPolicySupport.disablePlugin(plugin)
                        } else {
                            nafPolicySupport.enablePlugin(plugin)
                        }
                    }
                }

            includesRegex.forEach { regex ->
                kotlin.runCatching {
                    context.addIncludeInContextRegex(regex)
                }
            }

            excludesRegex.forEach { regex ->
                kotlin.runCatching {
                    context.addExcludeFromContextRegex(regex)
                    session.addExcludeFromScanRegexs(regex)
                    session.addExcludeFromSpiderRegex(regex)

                    // Hmm... ?
                    // session.addExcludeFromProxyRegex(regex)
                }
            }

            context.techSet = TechSet(includeTech.toTypedArray(), excludeTech.toTypedArray())
        }

        return nafScanContext.copy(context = context, policy = policy, user = user)
    }


    private val httpSender by lazy {
        HttpSender(
            model.optionsParam.connectionParam,
            true,
            HttpSender.MANUAL_REQUEST_INITIATOR
        )
    }
    private suspend fun getLoginNode(url: String): SiteNode {
        val msg = HttpMessage(URI(url, true))
        httpSender.sendAndReceive(msg, true)
        extensionHistory.addHistory(msg, HistoryReference.TYPE_PROXIED)

        withContext(Dispatchers.IO) {
            SwingUtilities.invokeAndWait {
                model.session.siteTree.addPath(msg.historyRef)
            }
        }


        val uri = URI(url, false)

        var target: org.zaproxy.zap.model.Target? = null

        for (index in 0..10) {
            val siteNode = model.session.siteTree.findNode(uri)

            if (siteNode != null) {
                target = org.zaproxy.zap.model.Target(siteNode, true)
                break
            }
            delay(200L)
        }

        if (target != null) {
            val id = extensionSpider
                .startScan(target, null, null)

            val scan = extensionSpider.getScan(id)

            while (scan.isRunning) {
                delay(100L)
            }
        }


        for (index in 0..10) {

            val parent = this.model
                .session
                .siteTree
                .findClosestParent(uri)

            val loginSiteNode = parent
                .children()
                .toList()
                .map { it as SiteNode }
                .firstOrNull { it.historyReference.uri == uri && it.historyReference.method == "POST" }

            if (loginSiteNode != null) {
                return loginSiteNode
            }

            delay(200L)
        }

        return model.session.siteTree.findNode(uri)
    }
}

private fun String.urlEncode() = URLEncoder.encode(this, StandardCharsets.UTF_8.name())

private fun <T : Any> T.setAndReturnPrivateProperty(variableName: String, data: Any): Any? {
    return javaClass.superclass.getDeclaredField(variableName).let { field ->
        field.isAccessible = true
        field.set(this, data)
        return@let field.get(this)
    }
}
