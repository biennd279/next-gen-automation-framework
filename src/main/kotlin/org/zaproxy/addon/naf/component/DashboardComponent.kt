package org.zaproxy.addon.naf.component

import androidx.compose.runtime.State
import com.arkivanov.decompose.ComponentContext
import org.zaproxy.addon.naf.NafScan
import org.zaproxy.addon.naf.NafState
import org.zaproxy.addon.naf.model.NafAlert

class DashboardComponent(
    componentContext: ComponentContext,
    nafState: NafState,
    val currentScan: State<NafScan?>,
    val sendAlert: (NafAlert) -> Unit
): ComponentContext by componentContext, NafState by nafState {

}