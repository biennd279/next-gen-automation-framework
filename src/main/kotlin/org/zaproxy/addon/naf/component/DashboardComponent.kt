package org.zaproxy.addon.naf.component

import com.arkivanov.decompose.ComponentContext
import org.zaproxy.addon.naf.NafState

class DashboardComponent(
    componentContext: ComponentContext,
    nafState: NafState
): ComponentContext by componentContext, NafState by nafState {

}