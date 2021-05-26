package global.msnthrp.whoo.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import global.msnthrp.whoo.R
import global.msnthrp.whoo.domain.GpsEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow


class LocationService : Service() {

    private lateinit var gps: GpsManager

    override fun onBind(intent: Intent?): IBinder? = null

    @ExperimentalCoroutinesApi
    override fun onCreate() {
        super.onCreate()
        gps = GpsManager(applicationContext)
        gps.callback = { gpsEvent: GpsEvent ->
            gpsEventsFlow.tryEmit(gpsEvent)
        }
        gps.startListening()

        showNotification()
    }

    override fun onDestroy() {
        gps.stopListening()
        super.onDestroy()
    }

    private fun showNotification() {
        val channelId = "whoo.location_foreground"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.location_foreground_channel_name)
            val descriptionText = getString(R.string.location_foreground_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val mChannel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(mChannel)
        }
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .build()
        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(1313, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        } else {
            startForeground(1313, notification)
        }
    }

    companion object {

        val gpsEvents: Flow<GpsEvent>
            get() = gpsEventsFlow

        private val gpsEventsFlow = MutableSharedFlow<GpsEvent>(replay = 1)

        fun launch(context: Context) {
            context.startService(Intent(context, LocationService::class.java))
        }
    }
}