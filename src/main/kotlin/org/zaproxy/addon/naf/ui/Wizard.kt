package org.zaproxy.addon.naf.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.parosproxy.paros.core.scanner.Category
import org.zaproxy.addon.naf.component.WizardComponent
import org.zaproxy.addon.naf.model.NafPlugin

@Preview
@Composable
fun Wizard(
    component: WizardComponent
) {

    val currentTab = remember { mutableStateOf(WizardTab.CRAWL) }

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
fun TableHeader(
    titles: List<String>,
    weights: List<Float>
) {
    require(titles.size == weights.size)
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        titles.forEachIndexed { index, title ->
            Text(
                text = title,
                modifier = Modifier
                    .border(1.dp, color = Color.Black)
                    .weight(weights[index]),
                fontWeight = FontWeight.Bold,
                style = typography.h6
            )
        }
    }
}

@Composable
internal fun RowScope.Policy(
    policy: MutableState<NafPlugin>
) {
    val weights = listOf(.1f, .55f, .2f, .2f)

    with(policy.value) {
        // TODO: fix bug checked
        Checkbox(
            checked = threshold != NafPlugin.Threshold.OFF,
            onCheckedChange = {
                threshold = if (it) {
                    NafPlugin.Threshold.DEFAULT
                } else {
                    NafPlugin.Threshold.OFF
                }
            },
            modifier = Modifier.weight(weights[0])
        )
        Text(
            text = name,
            modifier = Modifier
                .weight(weights[1])
        )
        Box(
            modifier = Modifier
                .weight(weights[2])
        ) {
            val expanded = remember { mutableStateOf(false) }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                @Suppress("deprecation")
                Text(text = threshold.name.lowercase().capitalize())
                IconButton(onClick = { expanded.value = true }) {
                    Icon(Icons.Default.ArrowDropDown, "More")
                }
            }

            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = {
                    expanded.value = false
                }
            ) {
                NafPlugin.Threshold.values().forEach {
                    DropdownMenuItem(onClick = {
                        threshold = it
                        expanded.value = false
                    }) {
                        @Suppress("deprecation")
                        Text(text = it.name.lowercase().capitalize())
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .weight(weights[3])
        ) {
            val expanded = remember { mutableStateOf(false) }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                @Suppress("deprecation")
                Text(text = strength.name.lowercase().capitalize(),)
                IconButton(onClick = { expanded.value = true }) {
                    Icon(Icons.Default.ArrowDropDown, "More")
                }
            }
            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = {
                    expanded.value = false
                }
            ) {
                @Suppress("deprecation")
                NafPlugin.Strength.values().forEach {
                    DropdownMenuItem(onClick = {
                        strength = it
                        expanded.value = false
                    }) {
                        Text(text = it.name.lowercase().capitalize())
                    }
                }
            }
        }
    }

}


@Preview
@Composable
fun Polices(
    policies: List<MutableState<NafPlugin>>
) {
    val weights = listOf(.1f, .5f, .2f, .2f)
    val titles = listOf("Enable", "Name", "Threshold", "Strength")

    Column {
        Text(
            text = "Policy Scan",
            modifier = Modifier.padding(10.dp),
            fontWeight = FontWeight.Bold,
            style = typography.h5
        )

        Divider(Modifier.padding(10.dp))

        val groupPolicy = policies.groupBy { it.value.category }
        val availableCategory = listOf(
            Category.INFO_GATHER,
            Category.BROWSER,
            Category.SERVER,
            Category.MISC,
            Category.INJECTION
        )
        val selectedCategory = remember { mutableStateOf(availableCategory.first()) }

        TabRow(selectedTabIndex = selectedCategory.value) {
            availableCategory.forEach { category ->
                Tab(
                    selected = selectedCategory.value == category,
                    onClick = {
                        selectedCategory.value = category
                    }
                ) {
                    Text(
                        text = Category.getName(category),
                        modifier = Modifier.padding(5.dp),
                    )
                }
            }
        }

        TableHeader(
            titles = titles,
            weights = weights
        )

        Spacer(Modifier.padding(5.dp))

        LazyColumn {

            groupPolicy[selectedCategory.value]?.let {
                items(it.size) { index ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.background(
                            if (index % 2 == 0)
                                Color.LightGray
                            else
                                Color.Transparent
                        )
                    ) {
                        Policy(it[index])
                    }
                }
            }
        }
    }
}

@Composable
fun LabelCheckBox(
    checkedState: MutableState<Boolean>,
    canCheck: Boolean = true,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checkedState.value,
            onCheckedChange = {
                checkedState.value = it
            },
            enabled = canCheck
        )
        content()
    }
}
