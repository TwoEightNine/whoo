package global.msnthrp.whoo.data.gps

import global.msnthrp.whoo.domain.GpsEvent
import global.msnthrp.whoo.domain.LocPoint
import global.msnthrp.whoo.interactor.gps.GpsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.cos
import kotlin.math.sin


class FakeGpsDataSource : GpsDataSource {

    override val gpsEvents: Flow<GpsEvent>
        get() = flow {
            var lastEvent: GpsEvent
            while (true) {
                (0..360).forEach { degrees ->
                    kotlinx.coroutines.delay(1000L)
                    val rad = degrees * Math.PI / 180
                    val lat = CENTER_LAT + RADIUS * cos(rad)
                    val lon = CENTER_LON + RADIUS * sin(rad)
                    val altitude = 100f + 5f * cos(rad)
                    val speed = 10f + 5f * cos(rad)
                    val distance = speed.toFloat()

                    val newEvent = GpsEvent(
                        locPoint = LocPoint(lat, lon),
                        speed = speed.toFloat(),
                        altitude = altitude,
                        distanceSincePrev = distance,
                        timeStamp = System.currentTimeMillis()
                    )
                    emit(newEvent)
                    lastEvent = newEvent
                }
            }

        }

    companion object {
        private const val CENTER_LAT = 51.404
        private const val CENTER_LON = 30.056
        private const val RADIUS = 0.013

    }
}