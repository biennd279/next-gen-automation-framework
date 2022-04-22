package org.zaproxy.addon.naf.model

import org.parosproxy.paros.core.scanner.Plugin.AlertThreshold
import org.parosproxy.paros.core.scanner.Plugin.AttackStrength

data class NafPlugin(
    val id: Int,
    val category: Int,
    val name: String,
    var threshold: Threshold,
    var strength: Strength,
) {

    enum class Threshold {
        OFF,
        DEFAULT,
        LOW,
        MEDIUM,
        HIGH
    }

    enum class Strength {
        INSANE,
        DEFAULT,
        LOW,
        MEDIUM,
        HIGH
    }
}


fun NafPlugin.Threshold.toAlertThreshold(): AlertThreshold {
    return when (this) {
        NafPlugin.Threshold.OFF -> AlertThreshold.OFF
        NafPlugin.Threshold.DEFAULT -> AlertThreshold.DEFAULT
        NafPlugin.Threshold.LOW -> AlertThreshold.LOW
        NafPlugin.Threshold.MEDIUM -> AlertThreshold.MEDIUM
        NafPlugin.Threshold.HIGH -> AlertThreshold.HIGH
    }
}

fun NafPlugin.Strength.toAttackStrength(): AttackStrength {
    return when (this) {
        NafPlugin.Strength.INSANE -> AttackStrength.INSANE
        NafPlugin.Strength.DEFAULT -> AttackStrength.DEFAULT
        NafPlugin.Strength.LOW -> AttackStrength.LOW
        NafPlugin.Strength.MEDIUM -> AttackStrength.MEDIUM
        NafPlugin.Strength.HIGH -> AttackStrength.HIGH
    }
}

fun AlertThreshold.toThreshold(): NafPlugin.Threshold {
    return when (this) {
        AlertThreshold.OFF -> NafPlugin.Threshold.OFF
        AlertThreshold.DEFAULT -> NafPlugin.Threshold.DEFAULT
        AlertThreshold.LOW -> NafPlugin.Threshold.LOW
        AlertThreshold.MEDIUM -> NafPlugin.Threshold.MEDIUM
        AlertThreshold.HIGH -> NafPlugin.Threshold.HIGH
    }
}

fun AttackStrength.toStrength(): NafPlugin.Strength {
    return when(this) {
        AttackStrength.INSANE -> NafPlugin.Strength.INSANE
        AttackStrength.DEFAULT -> NafPlugin.Strength.DEFAULT
        AttackStrength.LOW -> NafPlugin.Strength.LOW
        AttackStrength.MEDIUM -> NafPlugin.Strength.MEDIUM
        AttackStrength.HIGH -> NafPlugin.Strength.HIGH

    }
}
