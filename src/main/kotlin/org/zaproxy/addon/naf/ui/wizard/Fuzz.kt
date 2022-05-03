package org.zaproxy.addon.naf.ui.wizard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.io.File

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
