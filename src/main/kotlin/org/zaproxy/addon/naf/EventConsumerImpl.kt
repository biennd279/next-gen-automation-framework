package org.zaproxy.addon.naf

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.parosproxy.paros.control.Control
import org.parosproxy.paros.model.HistoryReferenceEventPublisher
import org.parosproxy.paros.model.SiteMapEventPublisher
import org.zaproxy.addon.naf.model.toNafAlert
import org.zaproxy.zap.eventBus.Event
import org.zaproxy.zap.eventBus.EventConsumer
import org.zaproxy.zap.extension.alert.AlertEventPublisher
import org.zaproxy.zap.extension.alert.ExtensionAlert
import org.zaproxy.zap.model.ScanEventPublisher
import kotlin.coroutines.CoroutineContext

// All event Consumer
internal class EventConsumerImpl(
    val nafState: NafState,
    override val coroutineContext: CoroutineContext = Dispatchers.Default
): EventConsumer, CoroutineScope {

    private val setHistoryRef = mutableSetOf<Int>()

    private val extensionAlert: ExtensionAlert by lazy {
        Control.getSingleton().extensionLoader.getExtension(ExtensionAlert::class.java)
    }

    override fun eventReceived(event: Event?) {
        when (event?.eventType) {
            // Append to list, only change when done
            HistoryReferenceEventPublisher.EVENT_TAG_ADDED -> {
                val id = event.parameters[HistoryReferenceEventPublisher.FIELD_HISTORY_REFERENCE_ID]?.toInt()
                id?.let {
                    if (!setHistoryRef.contains(id)) {
                        launch {
                            val historyReference = nafState.getHistoryReference(it)
                            setHistoryRef.add(id)
                            nafState.historyRefSate.update {
                                it +  historyReference
                            }
                        }
                    }
                }
            }
            SiteMapEventPublisher.SITE_ADDED_EVENT,
            SiteMapEventPublisher.SITE_NODE_ADDED_EVENT -> {
                event.target?.let { it ->
                    if (it.isValid) {
//                        nafState.siteNodes.update {siteNodes ->
//                            siteNodes + it.toNafTarget().startNode
//                        }
                    }
                }
            }

            AlertEventPublisher.ALERT_ADDED_EVENT -> {
                kotlin.runCatching {
                    val map = event.parameters
                    val id = map[AlertEventPublisher.ALERT_ID]?.toIntOrNull()!!
                    val alert = extensionAlert
                        .allAlerts
                        .find { it.alertId == id }!!
                    nafState.alerts.update { alerts ->
                        alerts + alert.toNafAlert()
                    }
                }
            }

            // Add to set of changed, removed
            HistoryReferenceEventPublisher.EVENT_NOTE_SET,
            HistoryReferenceEventPublisher.EVENT_REMOVED,
            AlertEventPublisher.ALERT_CHANGED_EVENT -> {}


            HistoryReferenceEventPublisher.EVENT_TAGS_SET -> {
                val id = event.parameters[HistoryReferenceEventPublisher.FIELD_HISTORY_REFERENCE_ID]?.toInt()
                id?.let {
                    if (setHistoryRef.contains(id)) {
                        launch {
                            val historyReference = nafState.getHistoryReference(it)
                            nafState.historyRefSate.update { historyReferences ->
                                historyReferences.map { if (it.historyId == id) historyReference else it }
                            }
                        }
                    }
                }
            }
            HistoryReferenceEventPublisher.EVENT_TAG_REMOVED -> {
                val id = event.parameters[HistoryReferenceEventPublisher.FIELD_HISTORY_REFERENCE_ID]?.toInt()
                id?.let {
                    if (setHistoryRef.contains(id)) {
                        setHistoryRef.remove(id)
                        nafState.historyRefSate.update { historyReferences ->
                            historyReferences.filter { it.historyId != id }
                        }
                    }
                }
            }
            AlertEventPublisher.ALERT_REMOVED_EVENT -> {
                kotlin.runCatching {
                    val map = event.parameters
                    val id = map[AlertEventPublisher.ALERT_ID]!!

                    nafState.alerts.update { alerts ->
                        alerts.filter { it.id != id }
                    }
                }
            }
            SiteMapEventPublisher.SITE_REMOVED_EVENT,
            SiteMapEventPublisher.SITE_NODE_REMOVED_EVENT -> {
                event.target?.let { target ->
//                    if (target.isValid) {
//                        nafState.siteNodes.update { siteNodes ->
//                            val nafNode = target.toNafTarget().startNode
//                            siteNodes.filter { it != nafNode }
//                        }
//                    }
                }
            }

            // When notify change
            ScanEventPublisher.SCAN_STARTED_EVENT,
            ScanEventPublisher.SCAN_PROGRESS_EVENT,
            ScanEventPublisher.SCAN_PAUSED_EVENT,
            ScanEventPublisher.SCAN_RESUMED_EVENT,
            ScanEventPublisher.SCAN_COMPLETED_EVENT -> {}
            else -> {}
        }
    }
}