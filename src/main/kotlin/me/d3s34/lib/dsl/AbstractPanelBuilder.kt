package me.d3s34.lib.dsl

import org.parosproxy.paros.extension.AbstractPanel
import java.awt.Component
import javax.swing.Icon

//class AbstractPanelBuilder : ContainerBuilder<AbstractPanel>() {
//    var icon: Icon? = null
//
//    inline fun icon(icon: () -> Icon) {
//        this.icon = icon()
//    }
//
//    override fun internalBuild(component: AbstractPanel) {
//        super.internalBuild(component)
//        icon?.let { component.icon = it }
//    }
//
//    fun build(): AbstractPanel {
//        val abstractPanel = AbstractPanel()
//        internalBuild(abstractPanel)
//        return abstractPanel
//    }
//
//}
//
//fun abstractPanel(lambda: AbstractPanelBuilder.() -> Unit) =
//    AbstractPanelBuilder()
//        .apply(lambda)
//        .build()


fun abstractPanel(lambda: AbstractPanel.() -> Unit) =
    AbstractPanel()
        .apply(lambda)