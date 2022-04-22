package org.zaproxy.addon.naf.ui.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.zaproxy.addon.naf.component.ProjectComponent

@Composable
fun Project(
    component: ProjectComponent,
    onCallWizard: () -> Unit
) {
    Column {
        Divider(Modifier.padding(10.dp))

        TextButton(
            onClick = onCallWizard,
            modifier = Modifier
                .border(1.dp, Color.LightGray)
        ) {
            Text(
                text = "Create new scan",
                style = MaterialTheme.typography.subtitle1
            )
        }

        Divider(Modifier.padding(10.dp))

        Text(
            text = "Recent scan...",
            style = MaterialTheme.typography.subtitle1
        )
    }
}
