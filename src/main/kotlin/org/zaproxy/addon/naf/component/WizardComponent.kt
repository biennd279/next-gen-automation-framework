package org.zaproxy.addon.naf.component

import com.arkivanov.decompose.ComponentContext

class WizardComponent(
    componentContext: ComponentContext,
    val onCancel: () -> Unit
): ComponentContext by componentContext {

}