package org.zaproxy.addon.naf

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.parosproxy.paros.model.HistoryReferenceEventPublisher
import org.parosproxy.paros.model.SiteMapEventPublisher
import org.zaproxy.addon.naf.model.NafAlert
import org.zaproxy.addon.naf.model.toNafAlert
import org.zaproxy.zap.eventBus.Event
import org.zaproxy.zap.eventBus.EventConsumer
import org.zaproxy.zap.extension.alert.AlertEventPublisher
import org.zaproxy.zap.model.ScanEventPublisher
import kotlin.coroutines.CoroutineContext

// All event Consumer
internal class EventConsumerImpl(
    val nafState: NafState,
    override val coroutineContext: CoroutineContext = Dispatchers.Default
): EventConsumer, CoroutineScope {

    private val setHistoryRef = mutableSetOf<Int>()
    private val setAlerts = mutableSetOf<NafAlert>()

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
            SiteMapEventPublisher.SITE_ADDED_EVENT -> {
                event.target?.let { it ->
                    println(it.displayName)
                    if (it.isValid) {
                        launch {
                            nafState.siteNodes.update {siteNodes ->
                                siteNodes + it.startNode
                            }
                        }
                    }
                }
            }
            SiteMapEventPublisher.SITE_NODE_ADDED_EVENT -> {
                event.target?.let { it ->
                    if (it.isValid) {
                        nafState.siteNodes.update {siteNodes ->
                            siteNodes + it.startNode
                        }
                    }
                }
            }
            AlertEventPublisher.ALERT_ADDED_EVENT -> {
                kotlin.runCatching {
                    launch {
                        val map = event.parameters
                        val alert = map.toNafAlert()
                        if (!setAlerts.contains(alert)) {
                            setAlerts.add(alert)
                            nafState.alerts.update {
                                it + alert
                            }
                        }
                    }
                }
                    .onFailure {

                    }
            }

            // Add to set of changed, removed
            HistoryReferenceEventPublisher.EVENT_NOTE_SET,
            HistoryReferenceEventPublisher.EVENT_REMOVED,
            HistoryReferenceEventPublisher.EVENT_TAGS_SET,
            HistoryReferenceEventPublisher.EVENT_TAG_REMOVED -> {

            }
            AlertEventPublisher.ALERT_CHANGED_EVENT,
            AlertEventPublisher.ALERT_REMOVED_EVENT,
            AlertEventPublisher.ALL_ALERTS_REMOVED_EVENT -> {

            }
            SiteMapEventPublisher.SITE_REMOVED_EVENT -> {

            }
            SiteMapEventPublisher.SITE_NODE_REMOVED_EVENT -> {

            }

            // When notify change
            ScanEventPublisher.SCAN_STARTED_EVENT,
            ScanEventPublisher.SCAN_PROGRESS_EVENT,
            ScanEventPublisher.SCAN_PAUSED_EVENT,
            ScanEventPublisher.SCAN_RESUMED_EVENT,
            ScanEventPublisher.SCAN_COMPLETED_EVENT -> {

            }
            else -> {}
        }
    }
}