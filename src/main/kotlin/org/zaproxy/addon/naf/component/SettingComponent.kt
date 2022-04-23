package org.zaproxy.addon.naf.component

import com.arkivanov.decompose.ComponentContext
import org.zaproxy.addon.naf.NafService

class SettingComponent(
    component: ComponentContext,
    val nafService: NafService
): ComponentContext by component {


}