package org.zaproxy.addon.naf.ui.home

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.AwtWindow
import com.mikepenz.markdown.Markdown
import org.zaproxy.addon.naf.component.ReportComponent
import java.awt.FileDialog
import java.awt.Frame

@Composable
fun Report(component: ReportComponent) {

    val report = component.buildReport()
    Box(
        modifier = Modifier.fillMaxSize()
            .padding(10.dp)
    ) {
        val scrollState = rememberScrollState(0)
        val openSaveFile = remember { mutableStateOf(false) }

        if (openSaveFile.value) {
            FileDialog(
                onCloseRequest = {
                    openSaveFile.value = false

                    it?.let {
                        component.exportToPdf(it)
                    }
                }
            )
        }

        Box {
            Markdown(
                content = report,
                modifier = Modifier
                    .verticalScroll(scrollState)
            )
        }

        VerticalScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
            adapter = rememberScrollbarAdapter(scrollState)
        )

        Button(
            onClick = { openSaveFile.value = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
        ) {
            Text("Export to PDF")
        }

    }
}

@Composable
private fun FileDialog(
    parent: Frame? = null,
    onCloseRequest: (filePath: String?) -> Unit
) = AwtWindow(
    create = {
        object : FileDialog(parent, "Choose a file", SAVE) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    onCloseRequest("$directory$file")
                }
            }
        }
    },
    dispose = FileDialog::dispose
)