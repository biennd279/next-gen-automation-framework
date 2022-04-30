package org.zaproxy.addon.naf.ui.wizard

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.d3s34.nuclei.NucleiTemplate
import org.zaproxy.addon.naf.component.WizardComponent
import org.zaproxy.addon.naf.model.NafPlugin
import org.zaproxy.zap.model.Tech

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
                WizardTab.SYSTEM -> System(
                    component.useNuclei,
                    component.templates
                )
                WizardTab.TechSet -> TechSetOptions(
                    component.includeTech,
                    component.excludeTech
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

@Composable
fun System(
    useNuclei: MutableState<Boolean>,
    templates: SnapshotStateList<NucleiTemplate>
) {

    Column {
        LabelCheckBox(useNuclei) {
            Text(
                text = "Run Nuclei Scan",
                modifier = Modifier.padding(10.dp),
                fontWeight = FontWeight.Bold
            )
        }

        Divider(Modifier.padding(10.dp))

        if (useNuclei.value) {
            templates.forEach { template ->
                Text(
                    text = template.path
                )
            }
        }
    }
}

@Composable
fun TechSetOptions(
    includeTech: SnapshotStateList<Tech>,
    excludeTech: SnapshotStateList<Tech>
) {
    val scrollState = rememberScrollState(0)

    fun onTechChange(isEnable: Boolean, tech: Tech): Unit {
        if (isEnable) {
            includeTech.add(tech)
            excludeTech.remove(tech)

            techGroup[tech]?.let { childTech ->
                includeTech.addAll(childTech)
                excludeTech.removeAll(childTech.toSet())
            }
        } else {
            includeTech.remove(tech)
            excludeTech.add(tech)

            techGroup[tech]?.let { childTech ->
                includeTech.removeAll(childTech.toSet())
                excludeTech.addAll(childTech)
            }
        }
    }

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(10.dp)
    ) {
        topTechs.forEach { topTech ->
            val expanded = remember { mutableStateOf(false) }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = { expanded.value = !expanded.value }
                ) {
                    Icon(
                        Icons.Default.run {
                            if (expanded.value) {
                                KeyboardArrowUp
                            } else {
                                KeyboardArrowDown
                            }
                        },
                        "Show child"
                    )
                }

                LabelCheckBox(
                    checkedState = includeTech.contains(topTech) || techGroup[topTech]?.let { includeTech.containsAll(it) } ?: false,
                    onCheckedChange = {
                        onTechChange(it, topTech)
                    }
                ) {
                    Text(
                        text = topTech.uiName,
                        style = typography.subtitle1
                    )
                }
            }

            if (expanded.value) {
                techGroup[topTech]?.forEach { tech ->
                    LabelCheckBox(
                        checkedState = includeTech.contains(tech) || techGroup[tech]?.let { includeTech.containsAll(it) } ?: false,
                        onCheckedChange = {
                            onTechChange(it, tech)
                        },
                        modifier = Modifier
                    ) {
                        Text(text = tech.uiName)
                    }
                }
            }

            Divider()
        }
    }
}

val techGroup = Tech.getAll()
    .groupBy { it.parent }

val topTechs: Set<Tech> = Tech.getTopLevel()

