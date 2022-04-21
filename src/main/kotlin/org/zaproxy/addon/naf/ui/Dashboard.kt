package org.zaproxy.addon.naf.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.parosproxy.paros.model.HistoryReference
import org.parosproxy.paros.model.SiteNode
import org.zaproxy.addon.naf.component.DashboardComponent
import org.zaproxy.addon.naf.model.NafAlert

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
            DashboardTab.PROCESS -> Processing()
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
            key = {
                listHistory.value[it].historyId
            }
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
    LazyColumn {
        items(alerts.value) {
            Row {
                Text(it.name)
                Spacer(modifier = Modifier.padding(5.dp))
                Text(it.uri)
                Spacer(modifier = Modifier.padding(5.dp))
                Text(it.param)
                Spacer(modifier = Modifier.padding(5.dp))
                Text(it.riskString)
                Spacer(modifier = Modifier.padding(5.dp))
                Text(it.confidenceString)
                Spacer(modifier = Modifier.padding(5.dp))
                Text(it.source.toString())
            }
        }
    }
}

@Composable
fun Processing() {

}
