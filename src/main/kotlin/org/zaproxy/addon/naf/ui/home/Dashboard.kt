package org.zaproxy.addon.naf.ui.home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.Markdown
import org.parosproxy.paros.model.HistoryReference
import org.zaproxy.addon.naf.NafScan
import org.zaproxy.addon.naf.component.DashboardComponent
import org.zaproxy.addon.naf.model.NafAlert
import org.zaproxy.addon.naf.model.NafNode
import org.zaproxy.addon.naf.ui.MainColors

@Composable
fun Dashboard(
    component: DashboardComponent
) {
    Scaffold(
        topBar = {
            TabRow(
                selectedTabIndex = component.subTab.value.ordinal,
                modifier = Modifier.height(30.dp),
                backgroundColor = MainColors.secondary,
            ) {
                DashboardTab.values().forEachIndexed { index, tab ->
                    Tab(
                        selected = component.subTab.value.ordinal == index,
                        onClick = { component.subTab.value = tab },
                    ) {
                        Text(tab.title)
                    }
                }
            }
        },
    ) {
        when (component.subTab.value) {
            DashboardTab.PROCESS -> Processing(component.currentScan)
            DashboardTab.ALERT -> Alert(
                component.alerts.collectAsState(),
                component.addIssue,
                component::removeAlert,
                component.sendToSqlmap,
                component.sendToCommix,
                component.sendToRFI,
                component.sendToLFI
            )
            DashboardTab.CRAWL -> Crawl(component.historyRefSate.collectAsState())
            DashboardTab.SITEMAP -> SiteMap(component.siteNodes.collectAsState())
        }
    }
}

@Preview
@Composable
fun Crawl(
    listHistory: State<List<HistoryReference>>
) {

    LazyColumn {
        items(
            listHistory.value.size,
//            key = {
//                listHistory.value[it].historyId
//            }
        ) { index ->
            Row {
                Text(text = listHistory.value[index].historyId.toString())
                Spacer(modifier = Modifier.padding(5.dp))
                Text(text = listHistory.value[index].uri.toString())
                Spacer(modifier = Modifier.padding(5.dp))
                Text(text = listHistory.value[index].requestBody)
                Spacer(modifier = Modifier.padding(5.dp))
                Text(text = listHistory.value[index].statusCode.toString())
            }
        }
    }
}

@Composable
fun SiteMap(
    siteNodes: State<List<NafNode>>
) {
    LazyColumn {
        items(siteNodes.value) {
            Row {
                Text(it.name)
                Spacer(modifier = Modifier.padding(5.dp))
                Text(it.nodeName)
            }
        }
    }
}

@Composable
fun Alert(
    alerts: State<List<NafAlert>>,
    sendAlert: (NafAlert) -> Unit,
    removeAlert: (NafAlert) -> Unit,
    sendToSqlmap: (NafAlert) -> Unit,
    sendToCommix: (NafAlert) -> Unit,
    sendToRFI: (NafAlert) -> Unit,
    sendToLFI: (NafAlert) -> Unit
) {

    val currentAlert: MutableState<NafAlert?> = remember { mutableStateOf(null) }

    Row {
        Column(
            modifier = Modifier.weight(.4f)
        ) {

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Alerts",
                    style = typography.subtitle1,
                )
            }

            Divider()

            AlertList(
                alerts,
                onClickAlert = {
                    currentAlert.value = it
                },
                removeAlert = removeAlert,
                sendAlert,
                sendToSqlmap,
                sendToCommix,
                sendToRFI,
                sendToLFI
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        Column(
            modifier = Modifier.weight(.6f)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Detail",
                    style = typography.subtitle1,
                )
            }

            Divider()

            currentAlert.value?.let {
                AlertDetail(it)
            }
        }
    }
}

@Composable
fun AlertList(
    alerts: State<List<NafAlert>>,
    onClickAlert: (NafAlert) -> Unit,
    removeAlert: (NafAlert) -> Unit,
    sendAlert: (NafAlert) -> Unit,
    sendToSqlmap: (NafAlert) -> Unit,
    sendToCommix: (NafAlert) -> Unit,
    sendToRFI: (NafAlert) -> Unit,
    sendToLFI: (NafAlert) -> Unit
) {

    val stateVertical = rememberScrollState(0)

    LazyColumn(
        modifier = Modifier
            .horizontalScroll(stateVertical)
            .fillMaxWidth()
    ) {
        items(alerts.value.sortedWith(compareBy(NafAlert::risk, NafAlert::name).reversed())) {

            val expandedMenu = remember { mutableStateOf(false) }

            Row(
                modifier = Modifier
                    .clickable {
                        onClickAlert(it)
                    }
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    IconButton(
                        onClick = {
                            expandedMenu.value = true
                        }
                    ) {
                        Icon(Icons.Default.Send, "More")
                    }

                    DropdownMenu(
                        expanded = expandedMenu.value,
                        onDismissRequest = {
                            expandedMenu.value = false
                        },
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                expandedMenu.value = false
                                sendAlert(it)
                            }
                        ) {
                            Text("Add to Issue")
                        }

                        DropdownMenuItem(
                            onClick = {
                                expandedMenu.value = false
                                sendToSqlmap(it)
                            }
                        ) {
                            Text("Send to SQLMap")
                        }

                        DropdownMenuItem(
                            onClick = {
                                expandedMenu.value = false
                                sendToCommix(it)
                            }
                        ) {
                            Text("Send to Commix")
                        }

                        DropdownMenuItem(
                            onClick = {
                                expandedMenu.value = false
                                sendToLFI(it)
                            }
                        ) {
                            Text("Send to LFI exploiter")
                        }

                        DropdownMenuItem(
                            onClick = {
                                expandedMenu.value = false
                                sendToRFI(it)
                            }
                        ) {
                            Text("Send to RFI Exploiter")
                        }
                    }
                }

                Spacer(modifier = Modifier.width(5.dp))

                IconButton(
                    onClick = {
                        removeAlert(it)
                    }
                ) {
                    Icon(Icons.Default.Delete, "Remove")
                }

                Column {
                    Text(
                        text = it.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = typography.subtitle1,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = it.uri,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Divider()
        }
    }
}

@Composable
fun AlertDetail(
    alert: NafAlert
) {
    val stateVertical = rememberScrollState(0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(stateVertical)
    ) {
        Row {
            Text(
                text = alert.name,
                style = typography.subtitle1
            )
        }
        Divider(Modifier.padding(10.dp))

        AlertField(
            "URI",
            alert.uri
        )

        AlertField(
            "Risk",
            alert.riskString
        )

        AlertField(
            "Confidence",
            alert.confidenceString
        )

        AlertField(
            "Param",
            alert.param
        )

        AlertField(
            "CWE",
            alert.cweId.toString()
        )

        AlertTextField(
            "Description",
            alert.description
        )

        AlertTextField(
            "Solution",
            alert.solution
        )

        AlertTextField(
            "Other Info",
            alert.otherInfo
        )
    }
}

@Composable
fun AlertField(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        Text(
            text = title,
            style = typography.subtitle1,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.padding(5.dp, 0.dp, 20.dp, 0.dp))

        Text(
            text = value,
            style = typography.body1
        )
    }
}

@Composable
fun AlertTextField(
    title: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            style = typography.subtitle1,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.padding(5.dp, 0.dp, 20.dp, 0.dp))

        Markdown(content = text)
    }
}
@Composable
fun Processing(
    nafScan: State<NafScan?>
) {
    if (nafScan.value == null) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = "Not scan started",
                style = typography.h3
            )
        }
    } else {

        Spacer(modifier = Modifier.height(20.dp))

        Column{
            Row {
                Text(
                    text = "Pipeline",
                    style = typography.subtitle1,
                    modifier = Modifier.weight(.7f)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Status",
                    style = typography.subtitle1,
                    modifier = Modifier.weight(.3f)
                )
            }

            Divider()

            LazyColumn {
                nafScan.value!!.pipelineState.forEach { (pipeline, status) ->
                    item {
                        Row {
                            Text(
                                text = pipeline::class.simpleName ?: "",
                                style = typography.subtitle2,
                                modifier = Modifier.weight(.7f)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = status.name,
                                style = typography.subtitle1,
                                modifier = Modifier.weight(.3f)
                            )
                        }
                    }
                }
            }
        }
    }
}
