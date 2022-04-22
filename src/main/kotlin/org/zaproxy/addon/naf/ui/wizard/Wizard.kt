package org.zaproxy.addon.naf.ui.wizard

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.zaproxy.addon.naf.component.WizardComponent
import org.zaproxy.addon.naf.model.NafPlugin

@Preview
@Composable
fun Wizard(
    component: WizardComponent
) {

    val currentTab = remember { mutableStateOf(WizardTab.SCOPE) }

    Scaffold(
        topBar = {
            Text(
                text = "Nextgen Automation Framework",
                style = typography.h3,
                textAlign =  TextAlign.Center
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = component::startScan
                ) {
                    Text("Start Scan")
                }

                Spacer(
                    Modifier.padding(5.dp)
                )

                Button(
                    onClick = component.onCancel
                ) {
                    Text("Cancel")
                }
            }
        }
    ) {
        Column {
            InputUrl(component.url)

            Divider(
                color = Color.Gray,
                modifier = Modifier.padding(5.dp)
            )

            TabRow(
                selectedTabIndex = currentTab.value.ordinal,
                backgroundColor = Color.Transparent,
                modifier = Modifier.fillMaxWidth()
            ) {
                WizardTab.values().forEachIndexed { index, tab ->
                    Tab(
                        selected = index == currentTab.value.ordinal,
                        onClick = { currentTab.value = tab }
                    ) {
                        Text(tab.title)
                    }
                }
            }

            when (currentTab.value) {
                WizardTab.CRAWL -> CrawlOptions(
                    component.crawlSiteMap,
                    component.crawlAjax
                )
                WizardTab.SCAN -> ScanOptions(
                    component.activeScan,
                    component.nafPlugin
                )
                WizardTab.SCOPE -> Scope(
                    component.includesRegex,
                    component.exludesRegex,
                    WizardComponent::isValidRegex
                )
                else -> {}
            }
        }
    }
}

@Composable
fun InputUrl(
    url: MutableState<String>
) {
    OutlinedTextField(
        value = url.value,
        onValueChange = { url.value = it },
        label = { Text("URL") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}
@Composable
fun CrawlOptions(
    crawlSiteMap: MutableState<Boolean>,
    crawlAjax: MutableState<Boolean>
) {
    Column {
        LabelCheckBox(crawlSiteMap) {
            Text(
                text = "Crawl sitemap",
                modifier = Modifier.padding(10.dp),
                fontWeight = FontWeight.Bold
            )
        }

        LabelCheckBox(crawlAjax) {
            Text(
                text = "Crawl ajax",
                modifier = Modifier.padding(10.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ScanOptions(
    activeScan: MutableState<Boolean>,
    policies: List<MutableState<NafPlugin>>
) {
    Column {
        LabelCheckBox(activeScan) {
            Text(
                text = "Run Active Scan",
                modifier = Modifier.padding(10.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (activeScan.value) {
        Box(
            modifier = Modifier.padding(10.dp)
        ) {
            Polices(policies)
        }
    }
}

