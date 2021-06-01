package global.msnthrp.whoo.ui.trips

import androidx.lifecycle.viewModelScope
import global.msnthrp.whoo.data.trip.MemoryTripDataSource
import global.msnthrp.whoo.data.trip.room.RoomTripDataSource
import global.msnthrp.whoo.ui.base.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class TripsViewModel : BaseViewModel<TripsState>() {

    init {
        initState()
        viewModelScope.launch {
            async {
                RoomTripDataSource.tripsList.collect { tripList ->
                    updateState { it.copy(trips = tripList) }
                }
            }
        }
    }

    override fun createInitialState(): TripsState = TripsState.empty()
}