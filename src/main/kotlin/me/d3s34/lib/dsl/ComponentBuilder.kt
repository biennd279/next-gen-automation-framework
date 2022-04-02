package me.d3s34.lib.dsl

import java.awt.Component
import java.awt.Font

open class ComponentBuilder<in T : Component> {
    var name: String = ""
    var font: Font? = null

    inline fun name(name: () -> String) {
        this.name = name()
    }

    inline fun font(font: () -> Font) {
        this.font = font()
    }

    open fun internalBuild(component: T) {
        component.name = name
        font?.let { component.font = it }
    }
}
