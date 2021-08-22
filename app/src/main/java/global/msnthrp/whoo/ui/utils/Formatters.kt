package global.msnthrp.whoo.ui.utils

import global.msnthrp.whoo.ui.monitor.SpentTime
import kotlin.math.roundToInt


interface Formatter<From, To> {

    fun format(value: From, to: To): String
}

object DistanceFormatter : Formatter<Float, DistanceFormatter.DistanceMetrics> {

    override fun format(value: Float, to: DistanceMetrics): String {
        return when (to) {
            DistanceMetrics.KILOMETERS -> String.format("%.1f", value / 1000)
            DistanceMetrics.MILES -> String.format("%.1f", value / 1609.344)
        }
    }

    enum class DistanceMetrics {
        KILOMETERS,
        MILES
    }
}

object AltitudeFormatter : Formatter<Float, Nothing> {

    override fun format(value: Float, to: Nothing): String {
        return value.roundToInt().toString()
    }
}

object SpeedFormatter : Formatter<Float, SpeedFormatter.SpeedMetrics> {

    override fun format(value: Float, to: SpeedMetrics): String {
        return when (to) {
            SpeedMetrics.MPS -> String.format("%.1f", value)
            SpeedMetrics.KMPH -> String.format("%.1f", value * 3.6)
        }
    }

    enum class SpeedMetrics {
        MPS,
        KMPH
    }
}

object DurationFormatter : Formatter<Long, Unit> {
    override fun format(value: Long, to: Unit): String {
        val ms = value / 1000
        val spentSec = (ms % 60).toString().padStart(2, '0')
        val spentMin = ((ms / 60) % 60).toString().padStart(2, '0')
        val spentHours = ((ms / 3600)).toString().padStart(2, '0')

        return "$spentHours:$spentMin:$spentSec"
    }
}