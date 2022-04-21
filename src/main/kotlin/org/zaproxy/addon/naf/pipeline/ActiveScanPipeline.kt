package org.zaproxy.addon.naf.pipeline

import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.parosproxy.paros.core.scanner.Alert
import org.parosproxy.paros.core.scanner.HostProcess
import org.parosproxy.paros.core.scanner.ScannerListener
import org.parosproxy.paros.network.HttpMessage
import org.zaproxy.zap.extension.ascan.ExtensionActiveScan
import org.zaproxy.zap.model.Target
import kotlin.coroutines.CoroutineContext

class ActiveScanPipeline(
    override val coroutineContext: CoroutineContext
) : NafPipeline<Target, Any>(NafPhase.SCAN) {

    val refreshTime = 500L

    private val extensionActiveScan: ExtensionActiveScan by lazy {
        extensionLoader
            .getExtension(ExtensionActiveScan::class.java)
    }

    override suspend fun start(input: Target): Any {
        val startNode = input.startNode
        val rootNode = if (startNode.isLeaf && !startNode.parent.isRoot && !startNode.parent.parent.isRoot)
            startNode.parent
        else
            startNode

        val scanId = extensionActiveScan.startScan(rootNode)
        val scan = extensionActiveScan.getScan(scanId)

        scan.addScannerListener(object : ScannerListener{
            override fun scannerComplete(id: Int) {
            }

            override fun hostNewScan(id: Int, hostAndPort: String?, hostThread: HostProcess?) {

            }

            override fun hostProgress(id: Int, hostAndPort: String?, msg: String?, percentage: Int) {

            }

            override fun hostComplete(id: Int, hostAndPort: String?) {

            }

            override fun alertFound(alert: Alert?) {

            }

            override fun notifyNewMessage(msg: HttpMessage?) {

            }

        })

        do {
            delay(refreshTime)
        } while (isActive && scan.isRunning)

        if (!isActive && scan.isRunning) {
            scan.stopScan()
        }

        return scan.alertsIds
    }
}