package org.zaproxy.addon.naf

import androidx.compose.runtime.snapshots.SnapshotStateList
import org.parosproxy.paros.model.HistoryReference
import org.parosproxy.paros.model.SiteNode
import org.zaproxy.addon.naf.model.NafAlert

interface NafState {
    val getHistoryReference: (Int) -> HistoryReference
    val historyId: MutableSet<Int>
    val historyRefSate: SnapshotStateList<HistoryReference>

    val siteNodes: SnapshotStateList<SiteNode>

    val alerts: SnapshotStateList<NafAlert>
}
