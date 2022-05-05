package org.zaproxy.addon.naf.model

import me.d3s34.nuclei.NucleiTemplate
import org.zaproxy.zap.model.Tech
import java.io.File


data class ScanTemplate(
    val url: String,
    val excludesRegex: List<String> = emptyList(),
    val includesRegex: List<String> = emptyList(),
    val includeTech: Set<Tech> = setOf(*Tech.getAll().toTypedArray()),
    val excludeTech: Set<Tech> = emptySet(),
    val fuzzOptions: FuzzOptions = FuzzOptions(),
    val crawlOptions: CrawlOptions = CrawlOptions(),
    val systemOptions: SystemOptions = SystemOptions(),
    val authenticationOptions: AuthenticationOptions = AuthenticationOptions(),
    val scanOptions: ActiveScanOptions,
    val isValidate: Boolean = false,
)

data class CrawlOptions(
    val crawl: Boolean = false,
    val ajaxCrawl: Boolean = false,
)

data class ActiveScanOptions(
    val activeScan: Boolean = false,
    val plugins: List<NafPlugin>,
)

data class SystemOptions(
    val useNuclei: Boolean = false,
    val templates: List<NucleiTemplate> = emptyList()
)

data class FuzzOptions(
    val useBruteForce: Boolean = false,
    val files: List<File> = emptyList()
)

data class AuthenticationOptions(
    val method: NafAuthenticationMethod = NafAuthenticationMethod.None,
)

sealed class NafAuthenticationMethod(
    open val username: String,
    open val password: String
) {
    object None: NafAuthenticationMethod("", "")
    data class FormBased(
        override val username: String,
        override val password: String,
        val loginUrl: String,
        val loginPage: String,
        val loginField: Pair<String ,String>,
        val loginPattern: String,
        val logoutPattern: String
    ): NafAuthenticationMethod(username, password)
}

enum class NafAuthMethodType {
    NONE, FORM_BASED
}

fun emptyFormBased() = NafAuthenticationMethod.FormBased(
    "",
    "",
    "",
    "",
    Pair("username", "password"),
    "",
    ""
)
