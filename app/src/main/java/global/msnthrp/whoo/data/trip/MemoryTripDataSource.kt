package global.msnthrp.whoo.data.trip

import global.msnthrp.whoo.domain.Trip
import global.msnthrp.whoo.interactor.trip.TripDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


object MemoryTripDataSource : TripDataSource {

    private var trips = mutableListOf<Trip>()

    private val tripsMutableFlow = MutableStateFlow<List<Trip>>(trips)

    override val tripsList: Flow<List<Trip>>
        get() = tripsMutableFlow

    override fun getIdForNewTrip(): Int = 1

    override fun updateTrip(trip: Trip) {
        val index = trips.indexOfFirst { it.id == trip.id }
        trips[index] = trip
        tripsMutableFlow.value = trips
    }
}