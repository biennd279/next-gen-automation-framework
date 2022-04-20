package org.zaproxy.addon.naf.component

import androidx.compose.runtime.State
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.RouterState
import com.arkivanov.decompose.router.replaceCurrent
import com.arkivanov.decompose.router.router
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import org.zaproxy.addon.naf.NafState
import org.zaproxy.addon.naf.model.ScanTemplate
import org.zaproxy.addon.naf.model.emptyTemplate
import org.zaproxy.addon.naf.ui.NafTab

class HomeComponent(
    componentContext: ComponentContext,
    val currentScan: State<ScanTemplate>,
    val nafState: NafState,
    private val onCallWizard: () -> Unit
): ComponentContext by componentContext {

    private val router = router<Config, Child>(
        initialConfiguration = Config.Dashboard,
        handleBackButton = true,
        childFactory = ::createChild
    )

    val routerState: Value<RouterState<Config, Child>> = router.state

    fun onSelectedTab(nafTab: NafTab) {
        when (nafTab) {
            NafTab.DASHBOARD -> router.replaceCurrent(Config.Dashboard)
            NafTab.PROJECT -> router.replaceCurrent(Config.Project)
            else -> {}
        }
    }

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): Child = when (config) {
        Config.Dashboard -> Child.Dashboard(DashboardComponent(componentContext, nafState))
        Config.Project -> Child.Project(ProjectComponent(componentContext), onCallWizard)
    }

    init {
        if (currentScan.value != emptyTemplate()) {
            startScan()
        }
    }

    private fun startScan() {
        println("Start new scan")
    }

    private fun pauseScan() {

    }

    private fun resumeScan() {

    }

    private fun stopScan() {

    }


    sealed class Child(
        val nafTab: NafTab
    ) {
        data class Dashboard(val component: DashboardComponent): Child(NafTab.DASHBOARD)
        data class Project(
            val component: ProjectComponent,
            val onCallWizard: () -> Unit
        ): Child(NafTab.PROJECT)
    }

    sealed class Config: Parcelable {
        @Parcelize
        object Dashboard: Config()

        @Parcelize
        object Project: Config()
    }
}