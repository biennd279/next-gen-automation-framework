package org.zaproxy.addon.naf.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import org.zaproxy.addon.naf.component.Home

@OptIn(ExperimentalDecomposeApi::class)
@Preview
@Composable
fun Home(
    component: Home
) = Children(component.routerState) { router ->
    val child = router.instance
    Scaffold(
        topBar = {
            NafTopBar(
                nafTab = child.nafTab,
                onSelectedTab = {
                    component.onSelectedTab(it)
                }
            )
        },
        modifier = Modifier.padding(5.dp).fillMaxSize()
    ) {
        Divider()

        when (child) {
            is Home.Child.Dashboard -> Dashboard(child.component)
            is Home.Child.Project -> Project(child.component, child.onCallWizard)
        }
    }
}

@Composable
fun NafTopBar(
    nafTab: NafTab,
    onSelectedTab: (NafTab) -> Unit
) {
    TabRow(
        selectedTabIndex = nafTab.ordinal,
        modifier = Modifier.height(40.dp),
    ) {
        NafTab.values().forEachIndexed { index, tab ->
            Tab(
                selected = nafTab.ordinal == index,
                onClick = { onSelectedTab(tab) }
            ) {
                Text(tab.title)
            }
        }
    }
}
