package org.zaproxy.addon.naf

import kotlinx.coroutines.flow.MutableStateFlow
import me.d3s34.commix.CommixDockerEngine
import me.d3s34.nuclei.NucleiEngine
import me.d3s34.nuclei.NucleiNativeEngine
import me.d3s34.sqlmap.SqlmapApiEngine
import org.zaproxy.addon.naf.model.CommixEngineType
import org.zaproxy.addon.naf.model.NafConfig
import org.zaproxy.addon.naf.model.NucleiEngineType
import org.zaproxy.addon.naf.model.SqlmapEngineType
import kotlin.coroutines.CoroutineContext

class NafServiceImpl(
    override val nafConfig: MutableStateFlow<NafConfig>,
    val coroutineContext: CoroutineContext,
    override val saveConfig: () -> Unit,
): NafService {

    val home: String = System.getProperty("user.home")

    override val nucleiEngine: NucleiEngine?
        get() = when(nafConfig.value.nucleiEngineType) {
            NucleiEngineType.Native -> {
                NucleiNativeEngine(
                    path = "${home}/go/bin/nuclei",
                    coroutineContext = coroutineContext
                )
            }
            else -> null
        }

    override val nucleiRootTemplatePath: String
        get() = nafConfig.value.templateRootDir ?: "${home}/nuclei-templates/dns/"

    override val sqlmapUrl: String
        get() = nafConfig.value.sqlmapApiUrl ?: "http://127.0.0.1:8775/"

    override val sqlmapEngine: SqlmapApiEngine?
        get() = when (nafConfig.value.sqlmapEngineType) {
            SqlmapEngineType.API_WITH_DOCKER,
            SqlmapEngineType.API -> {
                SqlmapApiEngine(sqlmapUrl, coroutineContext)
            }
            else -> null
        }

    override val commixDockerEngine: CommixDockerEngine?
        get() = when (nafConfig.value.commixEngineType) {
            CommixEngineType.DOCKER -> CommixDockerEngine(coroutineContext)
            else -> null
        }
}