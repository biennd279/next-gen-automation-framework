package me.d3s34.lib.dsl

import java.awt.Component
import java.awt.Container
import java.awt.LayoutManager


open class ContainerBuilder<in T: Container>: ComponentBuilder<T>() {
    var layout: LayoutManager? = null
    var components = mutableListOf<Component>()

    inline fun layout(layout: () -> LayoutManager) {
        this.layout = layout()
    }

    inline fun add(component: () -> Component) {
        components.add(component())
    }

    override fun internalBuild(component: T) {
        super.internalBuild(component)
        layout?.let { component.layout = it }
        components.forEach { component.add(it) }
    }
}
