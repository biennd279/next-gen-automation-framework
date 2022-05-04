package org.zaproxy.addon.naf

import kotlinx.coroutines.flow.MutableStateFlow
import org.parosproxy.paros.model.HistoryReference
import org.zaproxy.addon.naf.model.NafAlert
import org.zaproxy.addon.naf.model.NafNode

interface NafState {
    val getHistoryReference: (Int) -> HistoryReference
    val historyRefSate: MutableStateFlow<List<HistoryReference>>
    val siteNodes: MutableStateFlow<List<NafNode>>
    val alerts: MutableStateFlow<List<NafAlert>>
}
