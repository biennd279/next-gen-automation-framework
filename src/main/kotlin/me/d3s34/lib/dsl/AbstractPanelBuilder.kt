package me.d3s34.lib.dsl

import org.parosproxy.paros.extension.AbstractPanel
import java.awt.CardLayout
import javax.swing.JTextPane

class AbstractPanelBuilder: ContainerBuilder() {

    fun build(): AbstractPanel {
        val abstractPanel = AbstractPanel()

        abstractPanel.name = name
        abstractPanel.layout = layout
        abstractPanel.icon = icon
        components.forEach { abstractPanel.add(it) }

        return abstractPanel
    }
}

fun abstractPanel(lambda: AbstractPanelBuilder.() -> Unit) =
    AbstractPanelBuilder()
        .apply(lambda)
        .build()

fun main() {
    abstractPanel {
        name = "bien"
        layout = CardLayout()
        add {
            JTextPane()
        }
    }
}
