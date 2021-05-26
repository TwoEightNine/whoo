package global.msnthrp.whoo.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import global.msnthrp.whoo.domain.GpsEvent
import global.msnthrp.whoo.domain.LocPoint

class GpsManager(private val context: Context) : GpsStatus.Listener {

    var callback: ((GpsEvent) -> Unit)? = null

    private val locationManager: LocationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    private val locationListener: LocationListener by lazy {
        GpsLocationListener()
    }

    private var lastLocation: Location? = null

    @SuppressLint("MissingPermission")
    fun startListening() {
        val criteria = Criteria().apply {
            accuracy = Criteria.ACCURACY_FINE
            isSpeedRequired = true
            isAltitudeRequired = true
            isBearingRequired = false
            isCostAllowed = true
            powerRequirement = Criteria.POWER_MEDIUM
        }
        val providers = mutableListOf<String>()
        val bestProvider = locationManager.getBestProvider(criteria, true)

        if (bestProvider != null && bestProvider.isNotEmpty()) {
            providers.add(bestProvider)
        } else {
            providers.addAll(locationManager.getProviders(true))
        }

        for (provider in providers) {
            locationManager.requestLocationUpdates(
                provider, gpsMinTime,
                gpsMinDistance, locationListener
            )
        }
    }

    fun stopListening() {
        try {
            locationManager.removeUpdates(locationListener)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onGpsStatusChanged(event: Int) {
        var Satellites = 0
        var SatellitesInFix = 0

        val timetofix = locationManager.getGpsStatus(null)!!.timeToFirstFix
        Log.i("GPs", "Time to first fix = $timetofix")
        for (sat in locationManager.getGpsStatus(null)!!.satellites) {
            if (sat.usedInFix()) {
                SatellitesInFix++
            }
            Satellites++
        }
        Log.i("GPS", "$Satellites Used In Last Fix ($SatellitesInFix)")
    }

    companion object {
        private const val gpsMinTime = 500L
        private const val gpsMinDistance = 0f
    }

    private inner class GpsLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            callback?.apply {
                val calcDistance = location.distanceFromLast()
                val distance = when {
                    calcDistance < 1 -> 0f
                    else -> calcDistance
                }

                val calcSpeed = location.speed
                val speed = when {
                    calcSpeed < 1 -> 0f
                    else -> calcSpeed
                }

                val locPoint = LocPoint(
                    location.latitude,
                    location.longitude
                )
                val event = GpsEvent(
                    locPoint,
                    speed,
                    location.altitude,
                    distance,
                    location.time
                )

                invoke(event)
            }
            lastLocation = location
        }

        override fun onProviderDisabled(provider: String) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

        private fun Location.distanceFromLast(): Float {
            val lastLocation = lastLocation ?: return 0f
            val results = FloatArray(1)
            Location.distanceBetween(
                lastLocation.latitude,
                lastLocation.longitude,
                latitude,
                longitude,
                results
            )
            return results[0]
        }
    }
}