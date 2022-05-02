package org.zaproxy.addon.naf.pipeline

import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.zaproxy.addon.naf.model.NafScanContext
import org.zaproxy.zap.extension.ascan.ExtensionActiveScan
import kotlin.coroutines.CoroutineContext

class ActiveScanPipeline(
    override val coroutineContext: CoroutineContext,
) : NafPipeline<Any>(NafPhase.SCAN) {

    val refreshTime = 500L

    private val extensionActiveScan: ExtensionActiveScan by lazy {
        extensionLoader
            .getExtension(ExtensionActiveScan::class.java)
    }

    override suspend fun start(nafScanContext: NafScanContext): Any {
        
        val activeScanContext = mutableListOf<Any>()

        nafScanContext.policy?.let {
            activeScanContext.add(it)
        }

        nafScanContext.context?.let {
            activeScanContext.add(it.techSet)
        }

        val scanId = extensionActiveScan.startScan(nafScanContext.target, nafScanContext.user, activeScanContext.toTypedArray())
        val scan = extensionActiveScan.getScan(scanId)

        do {
            delay(refreshTime)
        } while (isActive && scan.isRunning)

        if (!isActive && scan.isRunning) {
            scan.stopScan()
        }

        return scan.alertsIds
    }
}