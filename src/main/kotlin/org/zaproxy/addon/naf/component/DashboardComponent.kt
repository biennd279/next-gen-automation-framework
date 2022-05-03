package org.zaproxy.addon.naf.component

import androidx.compose.runtime.State
import com.arkivanov.decompose.ComponentContext
import org.zaproxy.addon.naf.NafScan
import org.zaproxy.addon.naf.NafState

class DashboardComponent(
    componentContext: ComponentContext,
    nafState: NafState,
    val currentScan: State<NafScan?>
): ComponentContext by componentContext, NafState by nafState {

}