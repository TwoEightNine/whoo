package global.msnthrp.whoo.data.gps

import global.msnthrp.whoo.domain.GpsEvent
import global.msnthrp.whoo.interactor.gps.GpsDataSource
import global.msnthrp.whoo.services.LocationService
import kotlinx.coroutines.flow.Flow


class ServiceGpsDataSource : GpsDataSource {

    override val gpsEvents: Flow<GpsEvent>
        get() = LocationService.gpsEvents
}