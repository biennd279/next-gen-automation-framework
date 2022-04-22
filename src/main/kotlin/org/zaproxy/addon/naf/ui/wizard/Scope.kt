package org.zaproxy.addon.naf.ui.wizard

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun Scope(
    includesRegex: SnapshotStateList<String>,
    excludeRegex: SnapshotStateList<String>,
    isValidRegex: (String) -> Boolean
) {
    val isExclude = remember { mutableStateOf(true) }
    val isInclude = derivedStateOf { !isExclude.value }
    val currentRegex= remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            OutlinedTextField(
                value = currentRegex.value,
                onValueChange = {
                    currentRegex.value = it
                },
                singleLine = true,
                isError = hasError,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        text = "Regex: ",
                        style = typography.subtitle2
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (currentRegex.value.isEmpty()) {
                                hasError = false
                            } else {
                                if (isValidRegex(currentRegex.value)) {
                                    if (isInclude.value) {
                                        includesRegex.add(currentRegex.value)
                                    } else {
                                        excludeRegex.add(currentRegex.value)
                                    }
                                    currentRegex.value = ""
                                    hasError = false
                                } else {
                                    hasError = true
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, "Add regex")
                    }
                },
            )
        }

        Divider(Modifier.padding(10.dp))

        LazyColumn {
            if (isInclude.value) {
                items(includesRegex.size) { index ->
                    RegexItem(
                        regex = includesRegex[index],
                        index = index,
                        onRemoveRegex = {
                            includesRegex.removeAt(it)
                        }
                    )
                }
            } else {
                items(excludeRegex.size) { index ->
                    RegexItem(
                        regex = excludeRegex[index],
                        index = index,
                        onRemoveRegex = {
                            excludeRegex.removeAt(it)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RegexItem(
    regex: String,
    index: Int,
    onRemoveRegex: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = regex,
            style = typography.subtitle2,
            modifier = Modifier
                .padding(10.dp, 0.dp, 0.dp, 0.dp)
        )
        IconButton(
            onClick = { onRemoveRegex(index) }
        ) {
            Icon(Icons.Default.Delete, "remove regex")
        }

    }
}
