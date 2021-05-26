package global.msnthrp.whoo.interactor.trip

import global.msnthrp.whoo.domain.Trip
import kotlinx.coroutines.flow.Flow


interface TripDataSource {

    val tripsList: Flow<List<Trip>>

    fun getIdForNewTrip(): Int

    fun updateTrip(trip: Trip)

}