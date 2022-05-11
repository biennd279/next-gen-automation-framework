package org.zaproxy.addon.naf.ui.wizard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.d3s34.nuclei.NucleiTemplate
import me.d3s34.nuclei.NucleiTemplateDir


@Composable
fun System(
    useNuclei: MutableState<Boolean>,
    templates: SnapshotStateList<NucleiTemplate>,
    rootDir: NucleiTemplateDir
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
            LazyColumn {
                items(rootDir.getTemplates()) { template ->
                    LabelCheckBox(
                        checkedState = templates.contains(template),
                        onCheckedChange = {
                            if (it) {
                                templates.add(template)
                            } else {
                                templates.remove(template)
                            }
                        },
                        modifier = Modifier
                    ) {
                        Text(template.path.split("/").last())
                    }
                }
            }
        }
    }
}