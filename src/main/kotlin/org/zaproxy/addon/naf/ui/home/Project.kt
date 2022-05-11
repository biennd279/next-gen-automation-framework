package org.zaproxy.addon.naf.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.zaproxy.addon.naf.component.ProjectComponent

@Composable
fun Project(
    component: ProjectComponent,
    onCallWizard: () -> Unit
) {
    Column {
        Divider(Modifier.padding(10.dp))

        OutlinedButton(
            onClick = onCallWizard,
            border = BorderStroke(1.dp, MaterialTheme.colors.primary),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults
                .outlinedButtonColors(
                    contentColor = MaterialTheme.colors.primary,
                )
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
