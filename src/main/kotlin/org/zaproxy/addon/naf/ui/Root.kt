package org.zaproxy.addon.naf.ui

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import org.zaproxy.addon.naf.component.Root

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun Root(
    component: Root
) {
    Children(
        routerState = component.routerState
    ) {
        when (val child = it.instance) {
            is Root.Child.Wizard -> Wizard(child.component)
            is Root.Child.Home -> Home(child.component)
        }
    }
}
