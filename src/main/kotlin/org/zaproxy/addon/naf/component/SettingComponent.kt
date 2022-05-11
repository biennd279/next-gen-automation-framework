package org.zaproxy.addon.naf.component

import com.arkivanov.decompose.ComponentContext
import me.d3s34.docker.DockerClientManager
import org.zaproxy.addon.naf.NafService

class SettingComponent(
    component: ComponentContext,
    val nafService: NafService
): ComponentContext by component {
    val dockerManager = DockerClientManager()
}