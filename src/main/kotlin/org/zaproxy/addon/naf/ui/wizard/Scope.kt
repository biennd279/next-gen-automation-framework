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
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Text(
                    text = "Include to scope",
                    style = typography.subtitle1
                )
            }

            Divider()

            RegexColumn(includesRegex, isValidRegex)
        }

        Spacer(Modifier.width(10.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Text(
                    text = "Exclude from scope",
                    style = typography.subtitle1
                )
            }

            Divider()

            RegexColumn(excludeRegex, isValidRegex)
        }
    }
}

@Composable
fun RegexColumn(
    listRegex: SnapshotStateList<String>,
    isValidRegex: (String) -> Boolean
) {
    var hasError by remember { mutableStateOf(false) }
    val currentRegex= remember { mutableStateOf("") }

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
                                listRegex.add(currentRegex.value)
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
        items(listRegex.size) { index ->
            RegexItem(
                regex = listRegex[index],
                index = index,
                onRemoveRegex = {
                    listRegex.removeAt(it)
                }
            )
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
