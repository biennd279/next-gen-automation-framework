package org.zaproxy.addon.naf.component

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.RouterState
import com.arkivanov.decompose.router.pop
import com.arkivanov.decompose.router.push
import com.arkivanov.decompose.router.router
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.zaproxy.addon.naf.NafScanner
import org.zaproxy.addon.naf.NafState
import org.zaproxy.addon.naf.model.ScanTemplate
import org.zaproxy.addon.naf.model.emptyTemplate
import kotlin.coroutines.CoroutineContext

class Root internal constructor(
    componentContext: ComponentContext,
    val nafState: NafState,
    private val createWizard: (
        ComponentContext,
        onCancel: () -> Unit,
        onStartNewScan: (ScanTemplate) -> Unit,
    ) -> WizardComponent,
    private val createHome: (
        ComponentContext,
        NafState,
        currentScan: State<ScanTemplate>,
        onCallWizard: () -> Unit
    ) -> Home,
    override val coroutineContext: CoroutineContext
): CoroutineScope, ComponentContext by componentContext, NafState by nafState {

    constructor(componentContext: ComponentContext, nafState: NafState, coroutineContext: CoroutineContext): this(
        componentContext = componentContext,
        nafState = nafState,
        createWizard = { childContext, onCancel, onStartNewScan ->
            WizardComponent(childContext, onCancel, onStartNewScan)
        },
        createHome = { childContext, scanState, currentScan, onCallWizard ->
            Home(
                childContext,
                currentScan,
                scanState,
                onCallWizard
            )
        },
        coroutineContext
    )

    private val currentScan = mutableStateOf(emptyTemplate())

    private val router = router<Config, Child>(
        initialConfiguration = Config.Home,
        handleBackButton = true,
        childFactory = this::createChild
    )

    val routerState: Value<RouterState<Config, Child>> = router.state

    private fun onWizardCancel() {
        router.pop()
    }

    private fun onStartScan(scanTemplate: ScanTemplate) {
        currentScan.value = scanTemplate
        val scanner = NafScanner(
            scanTemplate,
            coroutineContext
        )
        launch {
            scanner.start()
        }

        router.pop()
    }

    private fun onCallNewWizard() {
        router.push(Config.Wizard)
    }

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): Child = when (config) {
        Config.Wizard -> Child.Wizard(createWizard(componentContext, this::onWizardCancel, this::onStartScan))
        Config.Home -> Child.Home(createHome(componentContext, nafState, currentScan, this::onCallNewWizard))
    }

    sealed class Child {
        data class Home(val component: org.zaproxy.addon.naf.component.Home): Child()
        data class Wizard(val component: WizardComponent): Child()
    }

    sealed class Config: Parcelable {
        @Parcelize
        object Wizard: Config()

        @Parcelize
        object Home: Config()
    }
}

