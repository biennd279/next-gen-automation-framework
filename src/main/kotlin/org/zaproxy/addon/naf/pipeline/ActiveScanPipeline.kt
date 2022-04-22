package org.zaproxy.addon.naf.pipeline

import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.zaproxy.zap.extension.ascan.ExtensionActiveScan
import org.zaproxy.zap.extension.ascan.ScanPolicy
import org.zaproxy.zap.model.Target
import kotlin.coroutines.CoroutineContext

class ActiveScanPipeline(
    val policy: ScanPolicy,
    override val coroutineContext: CoroutineContext,
) : NafPipeline<Target, Any>(NafPhase.SCAN) {

    val refreshTime = 500L

    private val extensionActiveScan: ExtensionActiveScan by lazy {
        extensionLoader
            .getExtension(ExtensionActiveScan::class.java)
    }

    override suspend fun start(input: Target): Any {
        
        val context = mutableListOf<Any>()

        context.add(policy)

        val scanId = extensionActiveScan.startScan(input, null, context.toTypedArray())
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