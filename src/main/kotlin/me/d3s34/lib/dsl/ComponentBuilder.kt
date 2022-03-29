package me.d3s34.lib.dsl

import java.awt.Font
import javax.swing.plaf.TextUI

open class ComponentBuilder {
    var name: String = ""
    var font: Font? = null

    inline fun name(name: () -> String) {
        this.name = name()
    }

    inline fun font(font: () -> Font) {
        this.font = font()
    }
    
}

open class TextComponentBuilder {
    var textUI: TextUI? = null
}
