package org.zaproxy.addon.naf

import kotlinx.coroutines.flow.MutableStateFlow
import org.parosproxy.paros.model.HistoryReference
import org.parosproxy.paros.model.SiteNode
import org.zaproxy.addon.naf.model.NafAlert

interface NafState {
    val getHistoryReference: (Int) -> HistoryReference
    val historyRefSate: MutableStateFlow<List<HistoryReference>>
    val siteNodes: MutableStateFlow<List<SiteNode>>
    val alerts: MutableStateFlow<List<NafAlert>>
}
