package org.zaproxy.addon.naf.component

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.RouterState
import com.arkivanov.decompose.router.replaceCurrent
import com.arkivanov.decompose.router.router
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.coroutines.CoroutineScope
import org.zaproxy.addon.naf.NafScan
import org.zaproxy.addon.naf.NafService
import org.zaproxy.addon.naf.NafState
import org.zaproxy.addon.naf.model.ExploitEvent
import org.zaproxy.addon.naf.model.NafEvent
import org.zaproxy.addon.naf.model.NopEvent
import org.zaproxy.addon.naf.ui.NafTab
import kotlin.coroutines.CoroutineContext

class HomeComponent(
    private val componentContext: ComponentContext,
    val currentScan: State<NafScan?>,
    private val nafState: NafState,
    private val nafService: NafService,
    private val onCallWizard: () -> Unit,
    override val coroutineContext: CoroutineContext
): ComponentContext by componentContext, CoroutineScope {

    private val listExploitTabComponent = mutableStateListOf<ExploitTabComponent>(StartTabComponent())

    private val router = router<Config, Child>(
        initialConfiguration = Config.Project,
        handleBackButton = true,
        childFactory = ::createChild
    )

    val routerState: Value<RouterState<Config, Child>> = router.state

    fun onSelectedTab(nafTab: NafTab) {
        when (nafTab) {
            NafTab.DASHBOARD -> router.replaceCurrent(Config.Dashboard)
            NafTab.PROJECT -> router.replaceCurrent(Config.Project)
            NafTab.SETTING -> router.replaceCurrent(Config.Setting)
            NafTab.EXPLOIT -> router.replaceCurrent(Config.Exploit)
            else -> {}
        }
    }

    fun sendEvent(nafEvent: NafEvent) {
        when (nafEvent) {
            is NopEvent -> {
                //TODO: Testing purpose only
                router.replaceCurrent(Config.Exploit)
            }
            is ExploitEvent -> {
                router.replaceCurrent(Config.Exploit)
                val child = routerState.value.activeChild.instance
                require(child is Child.Exploit)
                child.exploitComponent.handleExploitEvent(nafEvent)
            }
        }
    }

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): Child = when (config) {
        Config.Dashboard -> Child.Dashboard(DashboardComponent(componentContext, nafState))
        Config.Project -> Child.Project(ProjectComponent(componentContext), onCallWizard)
        Config.Setting -> Child.Setting(SettingComponent(componentContext, nafService))
        Config.Exploit -> Child.Exploit(ExploitComponent(
            componentContext,
            nafService,
            listExploitTabComponent,
            coroutineContext)
        )
    }

    sealed class Child(
        val nafTab: NafTab
    ) {
        data class Dashboard(val component: DashboardComponent): Child(NafTab.DASHBOARD)
        data class Project(
            val component: ProjectComponent,
            val onCallWizard: () -> Unit
        ): Child(NafTab.PROJECT)

        data class Setting(val componentContext: SettingComponent): Child(NafTab.SETTING)

        data class Exploit(val exploitComponent: ExploitComponent): Child(NafTab.EXPLOIT)
    }

    sealed class Config: Parcelable {
        @Parcelize
        object Dashboard: Config()
        @Parcelize
        object Project: Config()
        @Parcelize
        object Setting: Config()
        @Parcelize
        object Exploit: Config()
    }
}