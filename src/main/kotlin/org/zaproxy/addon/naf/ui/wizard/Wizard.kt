package org.zaproxy.addon.naf.ui.wizard

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.d3s34.nuclei.NucleiTemplate
import org.zaproxy.addon.naf.component.WizardComponent
import org.zaproxy.addon.naf.model.NafAuthMethodType
import org.zaproxy.addon.naf.model.NafAuthenticationMethod
import org.zaproxy.addon.naf.model.NafPlugin
import org.zaproxy.addon.naf.model.emptyFormBased
import org.zaproxy.zap.model.Tech
import java.io.File

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
                WizardTab.TECH_SET -> TechSetOptions(
                    component.includeTech,
                    component.excludeTech
                )
                WizardTab.FUZZ -> Fuzz(
                    component.useBruteForce,
                    component.files,
                    component.listBruteForceFile
                )
                WizardTab.AUTH -> Authentication(
                    component.nafAuthenticationMethod
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

@Composable
fun Fuzz(
    useBruteForce: MutableState<Boolean>,
    files: SnapshotStateList<File>,
    listBruteForceFile: List<File>
) {
    Column {
        LabelCheckBox(useBruteForce) {
            Text("Fuzzing")
        }

        if (useBruteForce.value) {
            LazyColumn {
                items(listBruteForceFile) { file ->
                    LabelCheckBox(
                        files.contains(file),
                        onCheckedChange = {
                            if (it) {
                                files.add(file)
                            } else {
                                files.remove(file)
                            }
                        }
                    ) {
                        Text(file.nameWithoutExtension)
                    }
                }
            }
        }
    }
}


@Composable
fun Authentication(
    nafAuthenticationMethod: MutableState<NafAuthenticationMethod>
) {
    val currentMethod = derivedStateOf {
        when (nafAuthenticationMethod.value) {
            NafAuthenticationMethod.None -> NafAuthMethodType.NONE
            is NafAuthenticationMethod.FormBased -> NafAuthMethodType.FORM_BASED
        }
    }

    Column {
        Row {
            val expanded = remember { mutableStateOf(false) }

            OutlinedTextField(
                value = currentMethod.value.name,
                onValueChange = {},
                trailingIcon = {
                    IconButton(
                        onClick = {
                            expanded.value = true
                        }
                    ) {
                        Icon(Icons.Default.ArrowDropDown, "show method")
                    }
                }
            )


            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = {
                    expanded.value = false
                }
            ) {
                NafAuthMethodType.values().forEach { nafAuthMethod ->
                    DropdownMenuItem(
                        onClick = {
                            expanded.value = false
                            when (nafAuthMethod) {
                                NafAuthMethodType.NONE -> {
                                    nafAuthenticationMethod.value = NafAuthenticationMethod.None
                                }

                                NafAuthMethodType.FORM_BASED -> {
                                    nafAuthenticationMethod.value = emptyFormBased()
                                }
                            }
                        }
                    ) {
                        Text(
                            text = nafAuthMethod.name
                        )
                    }
                }
            }
        }
    }


    when (nafAuthenticationMethod.value) {
        NafAuthenticationMethod.None -> {}
        is NafAuthenticationMethod.FormBased -> {
            @Suppress("unchecked_cast")
            FormBasedMethod(
                nafAuthenticationMethod as MutableState<NafAuthenticationMethod.FormBased>
            )
        }
    }

}

@Composable
fun FormBasedMethod(
    nafAuthenticationMethod: MutableState<NafAuthenticationMethod.FormBased>,
) {
    val scrollState = rememberScrollState(0)
    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
    ) {
        with(nafAuthenticationMethod) {
            OutlinedTextField(
                value = value.loginPage,
                onValueChange = {
                    value = value.copy(loginPage = it)
                },
                label = {
                    Text("Login page")
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = value.loginUrl,
                onValueChange = {
                    value = value.copy(loginUrl = it)
                },
                label = {
                    Text("Login Url")
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = value.username,
                onValueChange = {
                    value = value.copy(username = it)
                },
                label = {
                    Text("Username")
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = value.password,
                onValueChange = {
                    value = value.copy(password = it)
                },
                label = {
                    Text("Password")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = value.loginField.first,
                    onValueChange = {
                        value = value.copy(loginField = value.loginField.copy(first = it))
                    },
                    label = {
                        Text("Username Field")
                    }
                )

                OutlinedTextField(
                    value = value.loginField.second,
                    onValueChange = {
                        value = value.copy(loginField = value.loginField.copy(second = it))
                    },
                    label = {
                        Text("Password Field")
                    },
                )
            }
            OutlinedTextField(
                value = value.loginPattern,
                onValueChange = {
                    value = value.copy(loginPattern = it)
                },
                label = {
                    Text("Login Pattern")
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = value.logoutPattern,
                onValueChange = {
                    value = value.copy(logoutPattern = it)
                },
                label = {
                    Text("Logout Pattern")
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
