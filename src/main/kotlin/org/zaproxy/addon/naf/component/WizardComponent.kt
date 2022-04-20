package org.zaproxy.addon.naf.component

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import org.zaproxy.addon.naf.model.ScanTemplate

class WizardComponent(
    componentContext: ComponentContext,
    val onCancel: () -> Unit,
    val onWizardStart: (ScanTemplate) -> Unit
): ComponentContext by componentContext {
    val url = mutableStateOf("")

    private fun buildTemplate(): ScanTemplate {
        return ScanTemplate(
            url = url.value
        )
    }

    fun startScan() {
        val template = buildTemplate()
        onWizardStart.invoke(template)
    }
}