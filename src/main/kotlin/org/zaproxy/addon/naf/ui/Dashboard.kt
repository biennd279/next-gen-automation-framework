package org.zaproxy.addon.naf.ui

import androidx.compose.foundation.layout.height
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.zaproxy.addon.naf.component.DashboardComponent

@Composable
fun Dashboard(
    dashboardComponent: DashboardComponent
) {
    val subTab = remember { mutableStateOf(DashboardTab.ACTIVITY) }
    Scaffold(
        topBar = {
            TabRow(
                selectedTabIndex = subTab.value.ordinal,
                modifier = Modifier.height(30.dp)
            ) {
                DashboardTab.values().forEachIndexed { index, tab ->
                    Tab(
                        selected = subTab.value.ordinal == index,
                        onClick = { subTab.value = tab }
                    ) {
                        Text(tab.title)
                    }
                }
            }
        },
    ) {
        // TODO
    }
}