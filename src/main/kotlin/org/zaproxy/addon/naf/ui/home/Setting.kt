package org.zaproxy.addon.naf.ui.home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.zaproxy.addon.naf.component.SettingComponent
import org.zaproxy.addon.naf.model.*
import org.zaproxy.addon.naf.ui.MainColors
import org.zaproxy.addon.naf.ui.collectAsMutableState

@Composable
fun Setting(
    settingComponent: SettingComponent
) {
    Scaffold(
        topBar = {
            TabRow(
                selectedTabIndex = settingComponent.currentTab.value.ordinal,
                modifier = Modifier.height(30.dp),
                backgroundColor = MainColors.secondary
            ) {
                SettingTab.values().forEachIndexed { index, tab ->
                    Tab(
                        selected = settingComponent.currentTab.value.ordinal == index,
                        onClick = { settingComponent.currentTab.value = tab }
                    ) {
                        Text(
                            text = tab.title,
                            style = typography.subtitle1
                        )
                    }
                }
            }
        },
        bottomBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        settingComponent.nafService.saveConfig()
                    }
                ) {
                    Text("Save")
                }
            }
        }
    ) {

        Column {
            Divider(Modifier.padding(10.dp))

            val configState =  settingComponent.nafService.nafConfig.collectAsMutableState()

            when (settingComponent.currentTab.value) {
                SettingTab.NUCLEI -> NucleiSetting(configState)
                SettingTab.SQLMAP -> SqlmapSetting(
                    configState,
                    settingComponent::createSqlImage,
                    settingComponent::createSqlmapApiContainer,
                    settingComponent::startSqlmapApiContainer
                )
                SettingTab.COMMIX -> CommixSetting(
                    configState,
                    settingComponent::createCommixImage
                )
                SettingTab.TPLMAP -> TplmapSetting(
                    configState,
                    settingComponent::createTplmapImage
                )
            }
        }
    }
}


@Composable
fun SqlmapSetting(
    nafConfig: MutableState<NafConfig>,
    createImage: () -> Unit,
    createContainer: () -> Unit,
    startSqlmapApi: () -> Unit,
) {
    val isValidUri = remember { mutableStateOf<Boolean?>(null) }

    Column {
        Row(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = "Engine",
                style = typography.subtitle2,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.padding(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            SqlmapEngineType.values().forEach { engineType ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = nafConfig.value.sqlmapEngineType == engineType,
                        onClick = {
                            nafConfig.value = nafConfig.value.copy(sqlmapEngineType = engineType)
                        },
                        colors = RadioButtonDefaults.colors()
                    )
                    Text(
                        text = engineType.name
                    )
                }
            }
        }

        when (nafConfig.value.sqlmapEngineType) {
            SqlmapEngineType.API -> {

            }
            SqlmapEngineType.API_WITH_DOCKER -> {
                OutlinedTextField(
                    value = nafConfig.value.sqlmapApiUrl ?: "",
                    onValueChange = {
                        nafConfig.value = nafConfig.value.copy(sqlmapApiUrl = it)
                    },
                    label = {
                        Text(
                            text = "Uri",
                            style = typography.subtitle2,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                // Check Path

                                if (isValidUri.value == null) {
                                    isValidUri.value = true
                                } else {
                                    isValidUri.value = !isValidUri.value!!
                                }
                            }
                        ) {
                            Row {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Check Uri",
                                    tint = when (isValidUri.value) {
                                        null -> Color.Gray
                                        true -> Color.Green
                                        false -> Color.Red
                                    }
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row (
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = createImage
                    ) {
                        Text("Create sqlmap image")
                    }
                }

                Button(
                    onClick = createContainer
                ) {
                    Text("Create container")
                }

                Button(
                    onClick = startSqlmapApi
                ) {
                    Text("Start sqlmap api server")
                }
            }
            SqlmapEngineType.NONE -> {}
        }
    }
}

@Preview
@Composable
fun NucleiSetting(
    nafConfig: MutableState<NafConfig>
) {

    Column {

        Row(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = "Engine",
                style = typography.subtitle2,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.padding(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            NucleiEngineType.values().forEach { engineType ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = nafConfig.value.nucleiEngineType == engineType,
                        onClick = {
                            nafConfig.value = nafConfig.value.copy(nucleiEngineType = engineType)
                        },
                        colors = RadioButtonDefaults.colors()
                    )
                    Text(
                        text = engineType.name
                    )
                }
            }
        }

        when (nafConfig.value.nucleiEngineType) {
            NucleiEngineType.Native -> {
                Spacer(Modifier.padding(10.dp))

                Row(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = "Root Template",
                        style = typography.subtitle2,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.padding(10.dp))

                OutlinedTextField(
                    value = nafConfig.value.templateRootDir ?: "",
                    onValueChange = {
                        nafConfig.value = nafConfig.value.copy(templateRootDir = it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(
                            text = "Path",
                            style = typography.subtitle2,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Reset to default"
                    )
                    IconButton(
                        onClick = {

                        }
                    ) {
                        Icon(Icons.Default.Refresh, "Refresh path")
                    }
                }
            }
            NucleiEngineType.Docker -> {}
            NucleiEngineType.None -> {}
        }
    }
}

@Composable
fun CommixSetting(
    nafConfig: MutableState<NafConfig>,
    createImage: () -> Unit
) {
    Row(
        modifier = Modifier.padding(10.dp)
    ) {
        Text(
            text = "Engine",
            style = typography.subtitle2,
            fontWeight = FontWeight.Bold
        )
    }

    Spacer(Modifier.padding(10.dp))

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        CommixEngineType.values().forEach { engineType ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = nafConfig.value.commixEngineType == engineType,
                    onClick = {
                        nafConfig.value = nafConfig.value.copy(commixEngineType = engineType)
                    },
                    colors = RadioButtonDefaults.colors()
                )
                Text(
                    text = engineType.name
                )
            }
        }
    }

    when (nafConfig.value.commixEngineType) {
        CommixEngineType.NONE -> {

        }

        CommixEngineType.DOCKER -> {
            Button(
                onClick = createImage
            ) {
                Text("Create commix image")
            }
        }
        else -> {}
    }
}

@Composable
fun TplmapSetting(
    nafConfig: MutableState<NafConfig>,
    createImage: () -> Unit
) {

    Row(
        modifier = Modifier.padding(10.dp)
    ) {
        Text(
            text = "Engine",
            style = typography.subtitle2,
            fontWeight = FontWeight.Bold
        )
    }

    Spacer(Modifier.padding(10.dp))

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        TplmapEngineType.values().forEach { engineType ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = nafConfig.value.tplmapEngineType == engineType,
                    onClick = {
                        nafConfig.value = nafConfig.value.copy(tplmapEngineType = engineType)
                    },
                    colors = RadioButtonDefaults.colors()
                )
                Text(
                    text = engineType.name
                )
            }
        }
    }

    when (nafConfig.value.tplmapEngineType) {
        TplmapEngineType.NONE -> {

        }

        TplmapEngineType.DOCKER -> {
            Button(
                onClick = createImage
            ) {
                Text("Create tpl setting")
            }
        }
    }
}
