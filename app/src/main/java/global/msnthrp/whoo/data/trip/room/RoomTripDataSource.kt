package global.msnthrp.whoo.data.trip.room

import android.content.Context
import global.msnthrp.whoo.Application
import global.msnthrp.whoo.data.AppDatabase
import global.msnthrp.whoo.data.trip.room.model.entity.TripEntity
import global.msnthrp.whoo.domain.Trip
import global.msnthrp.whoo.interactor.trip.TripDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


object RoomTripDataSource : TripDataSource {

    private val tripDao = AppDatabase.getInstance(Application.context).tripDao()

    override val tripsList: Flow<List<Trip>>
        get() = tripDao.getAllTrips().map { it.toTripList() }

    override fun getIdForNewTrip(): Int {
        return tripDao.getMaxTripId().firstOrNull()?.inc() ?: 1
    }

    override fun updateTrip(trip: Trip) {
        tripDao.updateTrip(trip.toTripEntity())
    }

    private fun List<TripEntity>.toTripList(): List<Trip> = map { it.toTrip() }

    private fun TripEntity.toTrip(): Trip {
        return Trip(
            id = id,
            events = emptyList(),
            startTime = startTime,
            endTime = endTime,
            maxSpeed = maxSpeed,
            avgSpeed = avgSpeed,
            maxAltitude = maxAltitude,
            minAltitude = minAltitude,
            distance = distance
        )
    }

    private fun Trip.toTripEntity(): TripEntity {
        return TripEntity(
            id = id,
            startTime = startTime,
            endTime = endTime,
            maxSpeed = maxSpeed,
            avgSpeed = avgSpeed,
            maxAltitude = maxAltitude,
            minAltitude = minAltitude,
            distance = distance
        )
    }
}