package org.zaproxy.addon.naf

import me.d3s34.commix.CommixDockerEngine
import me.d3s34.nuclei.NucleiEngine
import me.d3s34.nuclei.NucleiNativeEngine
import me.d3s34.sqlmap.SqlmapApiEngine
import kotlin.coroutines.CoroutineContext

class NafServiceImpl(
    val coroutineContext: CoroutineContext
): NafService {
    val home: String = System.getProperty("user.home")

    override var nucleiEngine: NucleiEngine?= NucleiNativeEngine(
        path = "${home}/go/bin/nuclei",
        coroutineContext = coroutineContext
    )

    override var nucleiRootTemplatePath: String = "${home}/nuclei-templates/dns/"

    private val baseUrl: String = "http://127.0.0.1:8775/"
    override var sqlmapEngine: SqlmapApiEngine? = SqlmapApiEngine(baseUrl, coroutineContext)

    override var commixDockerEngine: CommixDockerEngine? = CommixDockerEngine(coroutineContext)
}