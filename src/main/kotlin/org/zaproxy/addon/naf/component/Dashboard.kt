package org.zaproxy.addon.naf.component

import com.arkivanov.decompose.ComponentContext
import org.zaproxy.addon.naf.NafState

class Dashboard(
    componentContext: ComponentContext,
    nafState: NafState
): ComponentContext by componentContext, NafState by nafState {

}