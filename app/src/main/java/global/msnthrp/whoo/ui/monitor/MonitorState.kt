package global.msnthrp.whoo.ui.monitor

import global.msnthrp.whoo.domain.LocPoint


data class MonitorState(
    val locPoint: LocPoint,
    val speed: Speed,
    val altitude: Altitude,

    val actualTrip: TripState
) {
    companion object {
        fun empty() = MonitorState(
            LocPoint(0.0, 0.0), Speed.empty(), Altitude.empty(), TripState.empty()
        )
    }
}

data class TripState(
    val maxSpeed: Speed,
    val distance: Distance,
    val minMaxAltitude: MinMaxAltitude,
    val isRunning: Boolean,
    val spentTime: SpentTime,
    val locPoints: List<LocPoint>
) {
    companion object {
        fun empty() = TripState(
            Speed.empty(),
            Distance.empty(),
            MinMaxAltitude.empty(),
            false,
            SpentTime.empty(),
            emptyList()
        )
    }
}

data class Speed(
    val speedRaw: Float,
    val speedMsUi: String,
    val speedKmphUi: String
) {
    companion object {
        fun empty() = Speed(0f, "0.0", "0.0")
    }
}

data class Distance(
    val distanceRaw: Float,
    val distanceKmUi: String,
    val distanceMilesUi: String
) {
    companion object {
        fun empty() = Distance(0f, "0.0", "0.0")
    }
}

data class Altitude(
    val altitudeMUi: String
) {
    companion object {
        fun empty() = Altitude("0")
    }
}

data class MinMaxAltitude(
    val minMaxAltitudeMUi: String
) {
    companion object {
        fun empty() = MinMaxAltitude("0/0")
    }
}

data class SpentTime(
    val spentRaw: Int,
    val spentUi: String
) {
    companion object {
        fun empty() = SpentTime(0, "00:00:00")
    }
}
