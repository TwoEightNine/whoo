package global.msnthrp.whoo.ui.trips

import global.msnthrp.whoo.domain.Trip


data class TripsState(
    val trips: List<Trip>
) {
    companion object {
        fun empty() = TripsState(emptyList())
    }
}