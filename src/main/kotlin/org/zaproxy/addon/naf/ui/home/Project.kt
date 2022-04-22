package org.zaproxy.addon.naf.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import org.zaproxy.addon.naf.component.ProjectComponent

@Composable
fun Project(
    component: ProjectComponent,
    onCallWizard: () -> Unit
) {
    Box {
        TextButton(onCallWizard) {
            Text("Create new scan")
        }
    }
}
