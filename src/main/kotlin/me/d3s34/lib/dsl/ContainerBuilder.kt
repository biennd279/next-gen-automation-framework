package me.d3s34.lib.dsl

import java.awt.Component
import java.awt.LayoutManager
import javax.swing.Icon


open class ContainerBuilder: ComponentBuilder() {
    var layout: LayoutManager? = null
    var icon: Icon? = null
    var components = mutableListOf<Component>()

    inline fun layout(layout: () -> LayoutManager) {
        this.layout = layout()
    }

    inline fun icon(icon: () -> Icon) {
        this.icon = icon()
    }

    inline fun add(component: () -> Component) {
        this.components.add(component())
    }

}
