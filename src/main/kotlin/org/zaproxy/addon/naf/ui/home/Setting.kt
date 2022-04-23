package org.zaproxy.addon.naf.ui.home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.zaproxy.addon.naf.component.SettingComponent
import org.zaproxy.addon.naf.model.NucleiEngineType
import org.zaproxy.addon.naf.ui.MainColors

@Composable
fun Setting(settingComponent: SettingComponent) {
    val currentTab = remember { mutableStateOf(SettingTab.NUCLEI) }

    Scaffold(
        topBar = {
            TabRow(
                selectedTabIndex = currentTab.value.ordinal,
                modifier = Modifier.height(30.dp),
                backgroundColor = MainColors.secondary
            ) {
                SettingTab.values().forEachIndexed { index, tab ->
                    Tab(
                        selected = currentTab.value.ordinal == index,
                        onClick = { currentTab.value = tab }
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
                    onClick = {}
                ) {
                    Text("Save")
                }
                TextButton(
                    onClick = {}
                ) {
                    Text("Cancel")
                }
            }
        }
    ) {

        Column {
            Divider(Modifier.padding(10.dp))

            when (currentTab.value) {
                SettingTab.NUCLEI -> NucleiSetting()
                else -> {}
            }
        }
    }
}

@Preview
@Composable
fun NucleiSetting() {
    val currentEngineType = remember { mutableStateOf(NucleiEngineType.None) }
    val path = remember { mutableStateOf("") }
    val isValidPath = remember { mutableStateOf<Boolean?>(null) }
    val templatePath = remember { mutableStateOf("") }

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
            NucleiEngineType.values().forEach {  engineType ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentEngineType.value == engineType,
                        onClick = { currentEngineType.value = engineType },
                        colors = RadioButtonDefaults.colors()
                    )
                    Text(
                        text = engineType.name
                    )
                }
            }
        }

        when (currentEngineType.value) {
            NucleiEngineType.None -> {

            }
            NucleiEngineType.Native -> {
                OutlinedTextField(
                    value = path.value,
                    onValueChange = { path.value = it },
                    label = {
                        Text(
                            text = "Path",
                            style = typography.subtitle2,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                // Check Path

                                if (isValidPath.value == null) {
                                    isValidPath.value = true
                                } else {
                                    isValidPath.value = !isValidPath.value!!
                                }
                            }
                        ) {
                            Row {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Check path",
                                    tint = when (isValidPath.value) {
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
            }
            NucleiEngineType.Docker -> {

            }
        }

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
            value = templatePath.value,
            onValueChange = { templatePath.value = it },
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
}
