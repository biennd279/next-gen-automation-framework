package org.zaproxy.addon.naf.component

import androidx.compose.runtime.MutableState
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
import org.zaproxy.addon.naf.NafScan
import org.zaproxy.addon.naf.NafScanner
import org.zaproxy.addon.naf.NafState
import org.zaproxy.addon.naf.model.ScanTemplate
import org.zaproxy.addon.naf.ui.NafTab
import kotlin.coroutines.CoroutineContext

class RootComponent internal constructor(
    componentContext: ComponentContext,
    val nafScanner: NafScanner,
    val nafState: NafState,
    private val createWizard: (
        ComponentContext,
        onCancel: () -> Unit,
        onStartNewScan: (ScanTemplate) -> Unit,
    ) -> WizardComponent,
    private val createHomeComponent: (
        ComponentContext,
        nafState: NafState,
        currentScan: MutableState<NafScan?>,
        onCallWizard: () -> Unit
    ) -> HomeComponent,
    override val coroutineContext: CoroutineContext
): CoroutineScope, ComponentContext by componentContext, NafState by nafState {

    constructor(
        componentContext: ComponentContext,
        nafScanner: NafScanner,
        nafState: NafState,
        coroutineContext: CoroutineContext
    ): this(
        componentContext = componentContext,
        nafScanner = nafScanner,
        nafState = nafState,
        createWizard = { childContext , onCancel, onStartNewScan ->
            WizardComponent(childContext, nafScanner, onCancel, onStartNewScan)
        },
        createHomeComponent = { childContext, scanState, currentScan, onCallWizard ->
            HomeComponent(
                childContext,
                currentScan,
                scanState,
                nafScanner,
                onCallWizard
            )
        },
        coroutineContext
    )

    private val currentScan: MutableState<NafScan?> = mutableStateOf(null)

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
        launch {
            val scan = nafScanner.parseScanTemplate(scanTemplate)
            currentScan.value = scan
            scan.start()
        }
        router.pop()
        val currentChild = router.state.value.activeChild.instance

        if (currentChild is Child.Home) {
            currentChild.component.onSelectedTab(NafTab.DASHBOARD)
        }
    }

    private fun onCallNewWizard() {
        router.push(Config.Wizard)
    }

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): Child = when (config) {
        Config.Wizard -> Child.Wizard(wizard(componentContext))
        Config.Home -> Child.Home(home(componentContext))
    }

    private fun wizard(componentContext: ComponentContext): WizardComponent =
        createWizard(
            componentContext,
            this::onWizardCancel,
            this::onStartScan,
        )

    private fun home(componentContext: ComponentContext): HomeComponent =
        createHomeComponent(
            componentContext,
            nafState,
            currentScan,
            this::onCallNewWizard
        )

    sealed class Child {
        data class Home(val component: HomeComponent): Child()
        data class Wizard(val component: WizardComponent): Child()
    }

    sealed class Config: Parcelable {
        @Parcelize
        object Wizard: Config()

        @Parcelize
        object Home: Config()
    }
}

