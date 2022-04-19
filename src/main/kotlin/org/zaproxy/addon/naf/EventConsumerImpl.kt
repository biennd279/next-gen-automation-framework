package org.zaproxy.addon.naf

import org.parosproxy.paros.model.HistoryReferenceEventPublisher
import org.parosproxy.paros.model.SiteMapEventPublisher
import org.zaproxy.zap.eventBus.Event
import org.zaproxy.zap.eventBus.EventConsumer
import org.zaproxy.zap.extension.alert.AlertEventPublisher
import org.zaproxy.zap.model.ScanEventPublisher

// All event Consumer
internal object EventConsumerImpl: EventConsumer {
    override fun eventReceived(event: Event?) {
        when (event?.eventType) {
            HistoryReferenceEventPublisher.EVENT_NOTE_SET,
            HistoryReferenceEventPublisher.EVENT_REMOVED,
            HistoryReferenceEventPublisher.EVENT_TAGS_SET,
            HistoryReferenceEventPublisher.EVENT_TAG_ADDED,
            HistoryReferenceEventPublisher.EVENT_TAG_REMOVED -> {
                // TODO
            }

            AlertEventPublisher.ALERT_CHANGED_EVENT,
            AlertEventPublisher.ALERT_ADDED_EVENT,
            AlertEventPublisher.ALERT_REMOVED_EVENT -> {
                // TODO
            }
            AlertEventPublisher.ALL_ALERTS_REMOVED_EVENT -> {

            }
            SiteMapEventPublisher.SITE_REMOVED_EVENT,
            SiteMapEventPublisher.SITE_ADDED_EVENT -> {
                // TODO
            }

            SiteMapEventPublisher.SITE_NODE_ADDED_EVENT,
            SiteMapEventPublisher.SITE_NODE_REMOVED_EVENT -> {
                // TODO
            }

            ScanEventPublisher.SCAN_STARTED_EVENT,
            ScanEventPublisher.SCAN_PROGRESS_EVENT,
            ScanEventPublisher.SCAN_PAUSED_EVENT,
            ScanEventPublisher.SCAN_RESUMED_EVENT,
            ScanEventPublisher.SCAN_COMPLETED_EVENT -> {
                // TODO
            }
            else -> {}
        }
    }
}