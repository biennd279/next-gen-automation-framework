package org.zaproxy.addon.naf.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun Wizard(
) {
    val onClickStart: () -> Unit = {}
    val onClickCancel: () -> Unit = {}
    val url = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Text(
                text = "Nextgen Automation Framework",
                style = typography.h3,
                textAlign =  TextAlign.Center
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onClickStart
                ) {
                    Text("Start Scan")
                }

                Spacer(
                    Modifier.padding(5.dp)
                )

                Button(
                    onClick = onClickCancel
                ) {
                    Text("Cancel")
                }
            }
        }
    ) {
        Column {
            InputUrl(url)

            Divider(
                color = Color.Gray,
                modifier = Modifier.padding(5.dp)
            )

            Tab(
                selected = true,
                onClick = {}
            ) {
                Text("Tab")
            }

            Tab(
                selected = false,
                onClick = {}
            ) {
                Text("Tab")
            }
        }
    }
}

@Preview
@Composable
fun InputUrl(
    url: MutableState<String>
) {
    OutlinedTextField(
        value = url.value,
        onValueChange = { url.value = it },
        label = { Text("URL") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun OptionWizard() {

}
