package me.d3s34.lib.dsl

import javax.swing.JTextPane
import javax.swing.text.JTextComponent

open class JTextComponentBuilder<in T : JTextComponent> : ComponentBuilder<T>() {
    var isEditable: Boolean? = null

    inline fun isEditable(isEditable: () -> Boolean) {
        this.isEditable = isEditable()
    }

    override fun internalBuild(component: T) {
        super.internalBuild(component)
        isEditable?.let { component.isEditable = it }
    }
}

class JTextPanelBuilder : JTextComponentBuilder<JTextPane>() {
    var contentType: String? = null
    var text: String? = null

    inline fun contentType(contentType: () -> String) {
        this.contentType = contentType()
    }

    inline fun text(text: () -> String) {
        this.text = text()
    }

    override fun internalBuild(component: JTextPane) {
        super.internalBuild(component)
        contentType?.let { component.contentType = it }
        text?.let { component.text = it }
    }

    fun build(): JTextPane {
        val jTextPane = JTextPane()
        internalBuild(jTextPane)
        return jTextPane
    }
}

fun jTextPanel(lambda: JTextPanelBuilder.() -> Unit): JTextPane = JTextPanelBuilder()
    .apply(lambda)
    .build()
