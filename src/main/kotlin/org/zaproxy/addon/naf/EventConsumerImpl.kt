package org.zaproxy.addon.naf

import org.parosproxy.paros.model.HistoryReferenceEventPublisher
import org.parosproxy.paros.model.SiteMapEventPublisher
import org.zaproxy.addon.naf.model.toNafAlert
import org.zaproxy.zap.eventBus.Event
import org.zaproxy.zap.eventBus.EventConsumer
import org.zaproxy.zap.extension.alert.AlertEventPublisher
import org.zaproxy.zap.model.ScanEventPublisher

// All event Consumer
internal class EventConsumerImpl(
    val nafState: NafState
): EventConsumer {
    override fun eventReceived(event: Event?) {
        when (event?.eventType) {
            // Append to list, only change when done
            HistoryReferenceEventPublisher.EVENT_TAG_ADDED -> {
                val id = event.parameters[HistoryReferenceEventPublisher.FIELD_HISTORY_REFERENCE_ID]?.toInt()
                id?.let {
                    if (!nafState.historyId.contains(id)) {
                        val historyReference = nafState.getHistoryReference(it)
                        nafState.historyId.add(id)
                        nafState.historyRefSate.add(historyReference)
                    }
                }
            }
            SiteMapEventPublisher.SITE_ADDED_EVENT -> {
                event.target?.let { it ->
                    println(it.displayName)
                    if (it.isValid) {
                        nafState.siteNodes.add(it.startNode)
                    }
                }
            }
            SiteMapEventPublisher.SITE_NODE_ADDED_EVENT -> {
                event.target?.let { it ->
                    if (it.isValid) {
                        nafState.siteNodes.add(it.startNode)
                    }
                }
            }
            AlertEventPublisher.ALERT_ADDED_EVENT -> {
                kotlin.runCatching {
                    val map = event.parameters
                    val alert = map.toNafAlert()
                    nafState.alerts.add(alert)
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