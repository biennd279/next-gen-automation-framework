package org.zaproxy.addon.naf.component

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import org.zaproxy.addon.naf.NafScan
import org.zaproxy.addon.naf.NafState
import org.zaproxy.addon.naf.model.NafAlert
import org.zaproxy.addon.naf.ui.home.DashboardTab

class DashboardComponent(
    componentContext: ComponentContext,
    nafState: NafState,
    val lastDashboardComponent: MutableState<DashboardTab>,
    val currentScan: State<NafScan?>,
    val addIssue: (NafAlert) -> Unit,
    val sendToSqlmap: (NafAlert) -> Unit,
    val sendToCommix: (NafAlert) -> Unit,
    val sendToRFI: (NafAlert) -> Unit,
    val sendToLFI: (NafAlert) -> Unit
): ComponentContext by componentContext, NafState by nafState {
    val subTab = lastDashboardComponent
}