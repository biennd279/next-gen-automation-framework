package org.zaproxy.addon.naf.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Preview
@Composable
fun Naf() {
    val nafTab = remember { mutableStateOf(NafTab.DASHBOARD) }

    MaterialTheme {
        Scaffold(
            topBar = { NafTopBar(nafTab) },
            modifier = Modifier.padding(5.dp)
        ) {
            Divider()
            when (nafTab.value) {
                NafTab.DASHBOARD -> Dashboard()
                else -> {}
            }
        }
    }
}

@Composable
fun NafTopBar(
    nafTab: MutableState<NafTab>
) {
    TabRow(
        selectedTabIndex = nafTab.value.ordinal,
        modifier = Modifier.height(40.dp),
        divider = {  }
    ) {
        NafTab.values().forEachIndexed { index, tab ->
            Tab(
                selected = nafTab.value.ordinal == index,
                onClick = { nafTab.value = tab }
            ) {
                Text(tab.title)
            }
        }
    }
}

@Composable
fun Dashboard(
    content: @Composable () -> Unit = {}
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
        }
    ) {
        content.invoke()
    }
}
