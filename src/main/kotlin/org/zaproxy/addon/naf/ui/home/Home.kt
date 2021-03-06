package org.zaproxy.addon.naf.ui.home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import org.zaproxy.addon.naf.component.HomeComponent
import org.zaproxy.addon.naf.ui.NafTab
import org.zaproxy.addon.naf.ui.exploit.Exploit

@OptIn(ExperimentalDecomposeApi::class)
@Preview
@Composable
fun Home(
    component: HomeComponent
) = Children(component.routerState) { router ->

    val child = router.instance

    Scaffold(
        topBar = {
            NafTopBar(
                nafTab = child.nafTab,
                onSelectedTab = { component.onSelectedTab(it) }
            )
        },
        modifier = Modifier.padding(5.dp).fillMaxSize()
    ) {

        Column {
            Divider()

            when (child) {
                is HomeComponent.Child.Dashboard -> Dashboard(child.component)
                is HomeComponent.Child.Project -> Project(child.component, child.onCallWizard)
                is HomeComponent.Child.Setting -> Setting(child.componentContext)
                is HomeComponent.Child.Exploit -> Exploit(child.exploitComponent)
                is HomeComponent.Child.Issue -> Issue(child.issueComponent)
                is HomeComponent.Child.Report -> Report(child.reportComponent)
            }
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
