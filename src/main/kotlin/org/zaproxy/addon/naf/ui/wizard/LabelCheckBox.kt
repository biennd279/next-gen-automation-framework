package org.zaproxy.addon.naf.ui.wizard

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LabelCheckBox(
    checkedState: MutableState<Boolean>,
    canCheck: Boolean = true,
    modifier: Modifier = Modifier.padding(8.dp),
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checkedState.value,
            onCheckedChange = {
                checkedState.value = it
            },
            enabled = canCheck
        )
        content()
    }
}

@Composable
fun LabelCheckBox(
    checkedState: State<Boolean>,
    onCheckedChange: (Boolean) -> Unit,
    canCheck: Boolean = true,
    modifier: Modifier = Modifier.padding(8.dp),
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checkedState.value,
            onCheckedChange = onCheckedChange,
            enabled = canCheck
        )
        content()
    }
}

@Composable
fun LabelCheckBox(
    checkedState: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    canCheck: Boolean = true,
    modifier: Modifier = Modifier.padding(8.dp),
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checkedState,
            onCheckedChange = onCheckedChange,
            enabled = canCheck
        )
        content()
    }
}

@Composable
fun LabelCheckBox(
    checkedState: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String,
    canCheck: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checkedState,
            onCheckedChange = onCheckedChange,
            enabled = canCheck
        )
        Text(
            text = text,
            modifier = modifier
        )
    }
}