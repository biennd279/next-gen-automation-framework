package org.zaproxy.addon.naf.pipeline

import kotlinx.coroutines.CoroutineScope
import org.parosproxy.paros.control.Control
import org.parosproxy.paros.extension.ExtensionLoader
import org.parosproxy.paros.model.Model
import org.zaproxy.addon.naf.model.NafScanContext

abstract class NafPipeline<out U>(
    val phase: NafPhase
): CoroutineScope {
    abstract suspend fun start(nafScanContext: NafScanContext): U

    val extensionLoader: ExtensionLoader = Control
        .getSingleton()
        .extensionLoader

    val model: Model = Model.getSingleton()

}
