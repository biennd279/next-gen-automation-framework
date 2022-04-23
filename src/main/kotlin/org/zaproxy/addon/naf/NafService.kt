package org.zaproxy.addon.naf

import me.d3s34.nuclei.NucleiEngine

interface NafService {
    var nucleiRootTemplatePath: String
    var nucleiEngine: NucleiEngine?
}