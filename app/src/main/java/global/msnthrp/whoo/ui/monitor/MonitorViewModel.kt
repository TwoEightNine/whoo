package global.msnthrp.whoo.ui.monitor

import androidx.lifecycle.viewModelScope
import androidx.room.Room
import global.msnthrp.whoo.data.gps.FakeGpsDataSource
import global.msnthrp.whoo.data.gps.ServiceGpsDataSource
import global.msnthrp.whoo.data.trip.MemoryTripDataSource
import global.msnthrp.whoo.data.trip.room.RoomTripDataSource
import global.msnthrp.whoo.domain.GpsEvent
import global.msnthrp.whoo.domain.Trip
import global.msnthrp.whoo.interactor.MonitorInteractor
import global.msnthrp.whoo.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt


class MonitorViewModel : BaseViewModel<MonitorState>() {

    private val mockGps = true
    private val gpsDataSource = when {
        mockGps -> FakeGpsDataSource()
        else -> ServiceGpsDataSource()
    }

    private val interactor = MonitorInteractor(
        gpsDataSource, RoomTripDataSource
    )

    init {
        initState()
        viewModelScope.launch {
            async {
                interactor.gpsEvents.collect(::onGpsEventCollected)
            }
            async {
                interactor.trip.collect(::onTripCollected)
            }
        }
    }

    override fun createInitialState(): MonitorState = MonitorState.empty()

    fun startStopTrip() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val tripStarted = uiState.value.actualTrip.isRunning
                if (tripStarted) {
                    interactor.stopTrip()
                } else {
                    interactor.startTrip()
                }
            }
        }
    }

    private fun onGpsEventCollected(gpsEvent: GpsEvent) {
        updateState { state ->
            state.copy(
                locPoint = gpsEvent.locPoint,
                speed = gpsEvent.speed.toSpeed(),
                altitude = gpsEvent.altitude.toAltitude()
            )
        }
    }

    private fun onTripCollected(trip: Trip) {
        updateState { state ->
            state.copy(
                actualTrip = TripState(
                    maxSpeed = trip.maxSpeed.toSpeed(),
                    distance = trip.distance.toDistance(),
                    minMaxAltitude = MinMaxAltitude(
                        "${trip.minAltitude.roundToInt()}/${trip.maxAltitude.roundToInt()}"
                    ),
                    spentTime = trip.startTime?.asStartTimeToSpendTime() ?: SpentTime.empty(),
                    isRunning = trip.isRunning,
                    locPoints = trip.events.map { it.locPoint }
                )
            )
        }
    }

    private fun Float.toSpeed() = Speed(
        speedRaw = this,
        speedMsUi = String.format("%.1f", this),
        speedKmphUi = String.format("%.1f", this * 3.6),
    )

    private fun Float.toDistance() = Distance(
        distanceRaw = this,
        distanceKmUi = String.format("%.1f", this / 1000),
        distanceMilesUi = String.format("%.1f", this / 1609.344),
    )

    private fun Double.toAltitude() = Altitude(
        altitudeMUi = roundToInt().toString()
    )

    private fun Long.asStartTimeToSpendTime(): SpentTime {
        val spentRaw = ((System.currentTimeMillis() - this) / 1000).toInt()
        val spentSec = (spentRaw % 60).toString().padStart(2, '0')
        val spentMin = ((spentRaw / 60) % 60).toString().padStart(2, '0')
        val spentHours = ((spentRaw / 3600)).toString().padStart(2, '0')

        return SpentTime(
            spentRaw =spentRaw,
            spentUi = "$spentHours:$spentMin:$spentSec"
        )
    }
}