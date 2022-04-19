package org.zaproxy.addon.naf.ui

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import org.zaproxy.addon.naf.component.RootComponent

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun Root(
    component: RootComponent
) {
    Children(
        routerState = component.routerState
    ) {
        when (val child = it.instance) {
            is RootComponent.Child.Wizard -> Wizard(child.component)
            is RootComponent.Child.Home -> Home(child.component)
        }
    }
}
