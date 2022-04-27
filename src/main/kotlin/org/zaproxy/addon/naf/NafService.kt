package org.zaproxy.addon.naf

import me.d3s34.commix.CommixDockerEngine
import me.d3s34.nuclei.NucleiEngine
import me.d3s34.sqlmap.SqlmapApiEngine

interface NafService {
    var nucleiRootTemplatePath: String
    var nucleiEngine: NucleiEngine?

    var sqlmapEngine: SqlmapApiEngine?
    var commixDockerEngine: CommixDockerEngine?
}