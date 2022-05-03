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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.parosproxy.paros.model.HistoryReference
import org.parosproxy.paros.model.SiteNode
import org.zaproxy.addon.naf.NafScan
import org.zaproxy.addon.naf.component.DashboardComponent
import org.zaproxy.addon.naf.model.NafAlert
import org.zaproxy.addon.naf.ui.MainColors

@Composable
fun Dashboard(
    component: DashboardComponent
) {

    val subTab = remember { mutableStateOf(DashboardTab.CRAWL) }
    Scaffold(
        topBar = {
            TabRow(
                selectedTabIndex = subTab.value.ordinal,
                modifier = Modifier.height(30.dp),
                backgroundColor = MainColors.secondary,
            ) {
                DashboardTab.values().forEachIndexed { index, tab ->
                    Tab(
                        selected = subTab.value.ordinal == index,
                        onClick = { subTab.value = tab },
                    ) {
                        Text(tab.title)
                    }
                }
            }
        },
    ) {
        when (subTab.value) {
            DashboardTab.CRAWL -> Crawl(component.historyRefSate.collectAsState())
            DashboardTab.SITEMAP -> SiteMap(component.siteNodes.collectAsState())
            DashboardTab.ALERT -> Alert(component.alerts.collectAsState())
            DashboardTab.PROCESS -> Processing(component.currentScan)
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
    siteNodes: State<List<SiteNode>>
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
    alerts: State<List<NafAlert>>
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

            AlertList(alerts) {
                currentAlert.value = it
            }
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
    onClickAlert: (NafAlert) -> Unit
) {
    val alertsByGroup = derivedStateOf { alerts.value.groupBy { it.name } }

    val collapsedState = alertsByGroup
        .value
        .keys
        .associateWith { true }
        .toList()
        .toMutableStateMap()

    val stateVertical = rememberScrollState(0)

    LazyColumn(
        modifier = Modifier.horizontalScroll(stateVertical)
    ) {
        alertsByGroup.value.forEach { (name, alerts) ->

            val collapsed = collapsedState[name]

            collapsed?.let {
                item(key = name) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                collapsedState[name] = !collapsed
                            }
                    ) {
                        Icon(
                            Icons.Default.run {
                                if (collapsed) {
                                    KeyboardArrowDown
                                } else {
                                    KeyboardArrowUp
                                }
                            },
                            contentDescription = "Expand alerts group"
                        )
                        Text(
                            text = name,
                            style = typography.subtitle1
                        )
                    }

                    Divider()
                }

                if (!collapsed) {
                    items(alerts) {
                        Row(
                            modifier = Modifier
                                .clickable {
                                    onClickAlert(it)
                                }
                        ) {
                            Text(
                                text = it.uri,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.padding(5.dp))
                        }
                    }
                }
            }
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
            style = typography.subtitle2
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
            style = typography.subtitle2
        )

        Spacer(Modifier.padding(5.dp, 0.dp, 20.dp, 0.dp))

        OutlinedTextField(
            value = text,
            onValueChange = {},
            readOnly = true
        )
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
