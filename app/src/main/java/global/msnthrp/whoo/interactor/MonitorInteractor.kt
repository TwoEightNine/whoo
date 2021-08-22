package global.msnthrp.whoo.interactor

import global.msnthrp.whoo.domain.GpsEvent
import global.msnthrp.whoo.domain.Trip
import global.msnthrp.whoo.interactor.gps.GpsDataSource
import global.msnthrp.whoo.interactor.trip.TripDataSource
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicBoolean


class MonitorInteractor(
    private val gpsDataSource: GpsDataSource,
    private val tripDataSource: TripDataSource
) {

    val gpsEvents: Flow<GpsEvent>
        get() = gpsDataSource.gpsEvents
            .map { gpsEvent ->
                updateTrip(gpsEvent)
                gpsEvent
            }

    val trip: Flow<Trip>
        get() = actualTripFlow

    private val tripStarted = AtomicBoolean(false)

    private var actualTrip = Trip()
    private val actualTripFlow = MutableSharedFlow<Trip>(replay = 1)


    fun startTrip() {
        if (tripStarted.compareAndSet(false, true)) {
            val newTripId = tripDataSource.getIdForNewTrip()
            val newTrip = Trip(
                id = newTripId,
                startTime = System.currentTimeMillis()
            )
            updateActualTrip {
                newTrip
            }
        }
    }

    fun stopTrip() {
        if (tripStarted.compareAndSet(true, false)) {
            updateActualTrip { trip ->
                trip.copy(
                    endTime = System.currentTimeMillis()
                )
            }
            tripDataSource.updateTrip(actualTrip)
        }
    }

    private fun updateTrip(gpsEvent: GpsEvent) {
        if (!tripStarted.get()) return

        updateActualTrip { trip ->
            val newMaxSpeed = when {
                gpsEvent.speed > trip.maxSpeed -> gpsEvent.speed
                else -> trip.maxSpeed
            }
            val newMaxAltitude = when {
                gpsEvent.altitude > trip.maxAltitude -> gpsEvent.altitude
                else -> trip.maxAltitude
            }
            val newMinAltitude = when {
                trip.events.isEmpty() -> gpsEvent.altitude
                gpsEvent.altitude < trip.minAltitude -> gpsEvent.altitude
                else -> trip.minAltitude
            }

            val eventsBefore = trip.events.size
            val newAvgSpeed = (trip.avgSpeed * eventsBefore + gpsEvent.speed) / (eventsBefore + 1)

            val newDistance = trip.distance + gpsEvent.distanceSincePrev

            val newTrip = trip.copy(
                events = ArrayList(trip.events).apply { add(gpsEvent) },
                maxSpeed = newMaxSpeed,
                avgSpeed = newAvgSpeed,
                distance = newDistance,
                maxAltitude = newMaxAltitude,
                minAltitude = newMinAltitude
            )

            newTrip
        }
    }

    private fun updateActualTrip(updateBlock: (Trip) -> Trip) {
        actualTrip = updateBlock(actualTrip)
        check(actualTripFlow.tryEmit(actualTrip))
    }
}