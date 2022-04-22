package org.zaproxy.addon.naf.ui.wizard

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun Scope() {
    val isExclude = remember { mutableStateOf(true) }
    val isInclude = derivedStateOf { !isExclude.value }

    Column {
        Row {
            LabelCheckBox(isExclude) {
                Text("Exclude from scope")
            }
            Spacer(Modifier.padding(10.dp))

            LabelCheckBox(
                checkedState = isInclude,
                onCheckedChange = {
                    isExclude.value = false
                }
            ) {
                Text("Include to scope")
            }
        }
    }
}