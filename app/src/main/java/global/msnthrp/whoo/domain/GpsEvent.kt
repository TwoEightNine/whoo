package global.msnthrp.whoo.domain


data class GpsEvent(
    val locPoint: LocPoint,
    /**
     * meters per second
     */
    val speed: Float,

    /**
     * meters above sea level
     */
    val altitude: Double,

    /**
     * meters
     */
    val distanceSincePrev: Float,
    val timeStamp: Long
)