package me.d3s34.feature.nuclei

abstract class NucleiWrapper(
    var templateDir: String
) {
    abstract fun scan()
}