package global.msnthrp.whoo.domain


data class Trip(
    val id: Int = 0,
    val events: List<GpsEvent> = emptyList(),

    val startTime: Long? = null,
    val endTime: Long? = null,

    val maxSpeed: Float = 0f,
    val avgSpeed: Float = 0f,

    val maxAltitude: Double = 0.0,
    val minAltitude: Double = 0.0,

    val distance: Float = 0f
) {

    val isRunning: Boolean
        get() = startTime != null && !isFinished

    val isFinished: Boolean
        get() = endTime != null
}
