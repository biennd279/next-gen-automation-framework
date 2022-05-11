package org.zaproxy.addon.naf.component

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.d3s34.docker.DockerClientManager
import org.zaproxy.addon.naf.NafService
import org.zaproxy.addon.naf.ui.home.SettingTab
import kotlin.coroutines.CoroutineContext

class SettingComponent(
    component: ComponentContext,
    val nafService: NafService,
    val scope: CoroutineScope
): ComponentContext by component {
    val dockerManager = DockerClientManager()
    val currentTab = mutableStateOf(SettingTab.SQLMAP)

    fun createSqlImage() {
        scope.launch {
            dockerManager.createSqlmapImage()
        }
    }

    fun createSqlmapApiContainer() {
        scope.launch {
            dockerManager.createSqlmapApiContainer()
        }
    }

    fun startSqlmapApiContainer() {
        scope.launch {
            dockerManager.startSqlmapApiContainer()
        }
    }

    fun createCommixImage() {
        scope.launch {
            dockerManager.createCommixImage()
        }
    }

    fun createTplmapImage() {
        scope.launch {
            dockerManager.createTplmapImage()
        }
    }
}