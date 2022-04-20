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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
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
            DashboardTab.CRAWL -> Crawl(component.nafState.historyRefSate)
            DashboardTab.SITEMAP -> SiteMap(component.nafState.siteNodes)
            DashboardTab.ALERT -> Alert(component.nafState.alerts)
            DashboardTab.PROCESS -> Processing()
        }
    }
}

@Preview
@Composable
fun Crawl(
    listHistory: SnapshotStateList<HistoryReference>
) {
    LazyColumn {
        items(
            listHistory.size,
            key = {
                listHistory[it].historyId
            }
        ) { index ->
            Row {
                Text(text = listHistory[index].historyId.toString())
                Spacer(modifier = Modifier.padding(5.dp))
                Text(text = listHistory[index].uri.toString())
                Spacer(modifier = Modifier.padding(5.dp))
                Text(text = listHistory[index].requestBody)
                Spacer(modifier = Modifier.padding(5.dp))
                Text(text = listHistory[index].statusCode.toString())
            }
        }
    }
}

@Composable
fun SiteMap(
    siteNodes: SnapshotStateList<SiteNode>
) {
    LazyColumn {
        items(siteNodes) {
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
    alerts: SnapshotStateList<NafAlert>
) {
    LazyColumn {
        items(alerts) {
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
