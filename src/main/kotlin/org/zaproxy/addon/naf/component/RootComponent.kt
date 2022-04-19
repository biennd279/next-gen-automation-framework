package org.zaproxy.addon.naf.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.RouterState
import com.arkivanov.decompose.router.pop
import com.arkivanov.decompose.router.push
import com.arkivanov.decompose.router.router
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

class RootComponent internal constructor(
    componentContext: ComponentContext,
    private val createWizard: (ComponentContext, () -> Unit) -> WizardComponent,
    private val createHome: (ComponentContext, () -> Unit) -> HomeComponent
): ComponentContext by componentContext {

    constructor(componentContext: ComponentContext): this(
        componentContext = componentContext,
        createWizard = { childContext, onCancel ->
            WizardComponent(childContext, onCancel)
        },
        createHome = { childContext, onCreateNewScan ->
            HomeComponent(childContext, onCreateNewScan)
        }
    )

    private val router = router<Config, Child>(
        initialConfiguration = Config.DashBoard,
        handleBackButton = true,
        childFactory = ::createChild
    )

    val routerState: Value<RouterState<Config, Child>> = router.state

    private fun onWizardCancel() {
        router.pop()
    }

    private fun onWizardStart() {

    }

    private fun onCreateNewScan() {
        router.push(Config.Wizard)
    }

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): Child = when (config) {
        Config.Wizard -> Child.Wizard(createWizard(componentContext, ::onWizardCancel))
        Config.DashBoard -> Child.Home(createHome(componentContext, ::onCreateNewScan))
    }

    sealed class Child {
        data class Home(val component: HomeComponent): Child()
        data class Wizard(val component: WizardComponent): Child()
    }

    sealed class Config: Parcelable {
        @Parcelize
        object Wizard: Config()

        @Parcelize
        object DashBoard: Config()
    }
}

