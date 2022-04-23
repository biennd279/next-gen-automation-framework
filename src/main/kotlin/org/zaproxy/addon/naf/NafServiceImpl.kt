package org.zaproxy.addon.naf

import me.d3s34.nuclei.NucleiEngine
import me.d3s34.nuclei.NucleiNativeEngine
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
}