package org.zaproxy.addon.naf.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import org.zaproxy.addon.naf.component.IssueComponent
import org.zaproxy.addon.naf.model.NafIssue
import org.zaproxy.addon.naf.model.Severity
import org.zaproxy.addon.naf.model.emptyIssue

@Composable
fun Issue(component: IssueComponent) {

    Text(
        text = "List Issue",
        style = typography.h4
    )

    Divider()

    Spacer(Modifier.height(20.dp))

    val issues = component.issues.collectAsState()
    val selectedIssue = remember { mutableStateOf<NafIssue?>(null) }

    Column(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(issues.value) {issue ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedIssue.value = issue
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = issue.severity.name,
                        style = typography.subtitle1
                    )

                    Spacer(Modifier.padding(10.dp))

                    Text(
                        text = issue.name,
                        style = typography.subtitle1
                    )

                    Spacer(Modifier.padding(10.dp))

                    Text(text = issue.description.replace("\n", " "))
                }

                Divider()
            }
        }

        Button(
            onClick = {
                selectedIssue.value = emptyIssue()
            }
        ) {
            Text("New Issue")
        }
    }

    selectedIssue.value?.also {
        val issueState = mutableStateOf(it)
        EditIssueDialog(issueState) {
            if (issueState.value != it) {
                component.saveIssue(issueState.value)
            }

            selectedIssue.value = null
        }
    }
}

@Composable
fun EditIssueDialog(
    issue: MutableState<NafIssue>,
    onClosedClick: () -> Unit,
) {

    //TODO: fix lost focus

    val dialogState = DialogState(
        size = DpSize(768.dp, 1024.dp)
    )

    Dialog(
        title = "Issue",
        state = dialogState,
        onCloseRequest = onClosedClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            OutlinedTextField(
                value = issue.value.name,
                label = { Text("Name") },
                onValueChange = { issue.value = issue.value.copy(name = it) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1
            )

            Row {
                val expandedSeverity = remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = issue.value.severity.name,
                    label = { Text("Severity") },
                    onValueChange = {},
                    modifier = Modifier
                        .wrapContentSize(),
                    trailingIcon = {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            "Show severity",
                            modifier = Modifier.clickable {
                                expandedSeverity.value = true
                            }
                        )
                    }
                )

                if (expandedSeverity.value) {
                    DropdownMenu(
                        expanded = expandedSeverity.value,
                        onDismissRequest = {
                            expandedSeverity.value = false
                        }
                    ) {
                        Severity.values().forEach { severity ->
                            DropdownMenuItem(
                                onClick = {
                                    issue.value = issue.value.copy(severity = severity)
                                    expandedSeverity.value = false
                                }
                            ) {
                                Text(text = severity.name)
                            }
                        }
                    }
                }
            }

            Divider()

            OutlinedTextField(
                value = issue.value.description,
                label = { Text("Description") },
                onValueChange = { issue.value = issue.value.copy(description = it) },
                modifier = Modifier.fillMaxWidth().weight(1f),
            )

            Divider()

            OutlinedTextField(
                value = issue.value.reproduce,
                label = { Text("Reproduce") },
                onValueChange = { issue.value = issue.value.copy(reproduce = it) },
                modifier = Modifier.fillMaxWidth().weight(1f),
            )

            Divider()

            OutlinedTextField(
                value = issue.value.solution,
                label = { Text("Solution") },
                onValueChange = { issue.value = issue.value.copy(solution = it) },
                modifier = Modifier.fillMaxWidth().weight(1f),
            )

            Divider()

            OutlinedTextField(
                value = issue.value.note,
                label = { Text("Note") },
                onValueChange = { issue.value = issue.value.copy(note = it) },
                modifier = Modifier.fillMaxWidth().weight(1f),
            )

            Divider()
        }
    }
}
