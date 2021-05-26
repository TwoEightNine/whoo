package global.msnthrp.whoo.interactor.gps

import global.msnthrp.whoo.domain.GpsEvent
import kotlinx.coroutines.flow.Flow


interface GpsDataSource {

    val gpsEvents: Flow<GpsEvent>
}