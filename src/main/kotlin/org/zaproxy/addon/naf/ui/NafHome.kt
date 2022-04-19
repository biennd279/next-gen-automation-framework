package org.zaproxy.addon.naf.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun Naf() {
//    val pushWizard = remember { mutableStateOf(true) }
//
//    MaterialTheme(colors = MainColors) {
//        val nafTab = remember { mutableStateOf(NafTab.DASHBOARD) }
//        Scaffold(
//            topBar = { NafTopBar(nafTab) },
//            modifier = Modifier.padding(5.dp)
//        ) {
//            Divider()
//            when (nafTab.value) {
//                NafTab.DASHBOARD -> Dashboard()
//                else -> {}
//            }
//        }
//    }

    Wizard()
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
