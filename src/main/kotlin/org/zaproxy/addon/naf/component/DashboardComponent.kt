package org.zaproxy.addon.naf.component

import com.arkivanov.decompose.ComponentContext
import org.zaproxy.addon.naf.NafState

class DashboardComponent(
    componentContext: ComponentContext,
    val nafState: NafState
): ComponentContext by componentContext {

}