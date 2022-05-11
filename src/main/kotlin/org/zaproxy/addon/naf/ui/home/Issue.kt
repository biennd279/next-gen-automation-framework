package org.zaproxy.addon.naf.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import com.mikepenz.markdown.Markdown
import org.zaproxy.addon.naf.component.IssueComponent
import org.zaproxy.addon.naf.model.NafIssue
import org.zaproxy.addon.naf.model.Severity
import org.zaproxy.addon.naf.model.emptyIssue
import org.zaproxy.addon.naf.model.toMarkdown
import org.zaproxy.addon.naf.ui.collectAsMutableState

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Issue(
    component: IssueComponent,
) {


    val issues = component.issues.collectAsState()
    val selectedIssue = component.currentIssue.collectAsMutableState()


    Column(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "List Issue",
                style = typography.h4
            )

            Button(
                onClick = {
                    selectedIssue.value = emptyIssue()
                }
            ) {
                Text("New Issue")
            }
        }

        Divider()

        Spacer(Modifier.height(20.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(issues.value) {issue ->

                val showPreview = remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedIssue.value = issue
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    IconButton(
                        onClick = {
                            component.removeIssue(issue)
                        }
                    ) {
                        Icon(Icons.Default.Delete, "remove issue")
                    }

                    Spacer(Modifier.width(10.dp))

                    IconButton(
                        onClick = {
                            showPreview.value = true
                        }
                    ) {
                        Icon(Icons.Default.Search, "Preview")
                    }

                    Spacer(Modifier.width(10.dp))

                    Text(
                        text = issue.severity.name,
                        style = typography.subtitle1
                    )

                    Spacer(Modifier.width(10.dp))

                    Text(
                        text = issue.name,
                        style = typography.subtitle1
                    )

                    Spacer(Modifier.width(10.dp))

                    Text(
                        text = issue.description.replace("\n", " "),
                        maxLines = 3
                    )
                }

                val dialogState = DialogState(
                    size = DpSize(768.dp, 768.dp)
                )

                if (showPreview.value) {
                    Dialog(
                        title = "Preview",
                        state = dialogState,
                        onCloseRequest = {
                            showPreview.value = false
                        }
                    ) {
                        val scrollState = rememberScrollState(0)

                        Markdown(
                            issue.toMarkdown(),
                            modifier = Modifier
                                .verticalScroll(scrollState)
                                .padding(5.dp)
                        )
                    }
                }

                Divider()
            }
        }

        selectedIssue.value?.also {
            val issueState = mutableStateOf(it)
            EditIssueDialog(
                issueState,
                onSaveClick = {
                    component.saveIssue(issueState.value)
                    selectedIssue.value = null
                },
                onCancelClick = {
                    selectedIssue.value = null
                }
            )
        }
    }
}

@Composable
fun EditIssueDialog(
    issue: MutableState<NafIssue>,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {

    //TODO: fix lost focus

    val dialogState = DialogState(
        size = DpSize(768.dp, 1024.dp)
    )

    Dialog(
        title = "Issue",
        state = dialogState,
        onCloseRequest = onSaveClick
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

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Button(
                    onClick = { onSaveClick() }
                ) {
                    Text("Save")
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = { onCancelClick() }
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}
