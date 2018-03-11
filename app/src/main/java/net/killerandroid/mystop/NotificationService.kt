package net.killerandroid.mystop

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.media.RingtoneManager
import android.os.IBinder
import android.os.Vibrator
import android.support.annotation.StringRes
import android.support.v4.app.NotificationCompat
import android.util.Log

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

import net.killerandroid.mystop.notification.NotificationSettings
import net.killerandroid.mystop.util.StopLocationRequest

class NotificationService : Service() {

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var lastKnownLocation: Location? = null
    private var settings: NotificationSettings? = null

    override fun onCreate() {
        super.onCreate()
        val notification = createNotification(R.string.notification_text)
        startForeground(ONGOING_NOTIFICATION_ID, notification)
        settings = NotificationSettings(this, null)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun createNotification(@StringRes messageId: Int): Notification {
        val notificationIntent = Intent(this, MapsActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        return NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getText(R.string.app_name))
                .setContentText(getText(messageId))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notification)
                .build()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        requestLocationUpdates()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        stopSelf()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun notifyNearStop() {
        if (lastKnownLocation == null || !settings!!.shouldShowNotification(
                lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)) {
            showNotification(R.string.notification_text)
            return
        }
        showNotification(R.string.notification_near_stop_text)
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }

        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(500)
    }

    private fun showNotification(@StringRes messageId: Int) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(
                ONGOING_NOTIFICATION_ID,
                createNotification(messageId))
    }

    private fun requestLocationUpdates() {
        if (!settings!!.areNotificationsEnabled())
            return

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                lastKnownLocation = locationResult!!.lastLocation
                notifyNearStop()
            }
        }

        try {
            fusedLocationProviderClient!!.requestLocationUpdates(
                    StopLocationRequest().getRequest(),
                    locationCallback, null)
        } catch (e: SecurityException) {
            // assume that the check happened in the associated foreground activity
        }
    }

    companion object {

        val TAG = NotificationService::class.java.simpleName
        val CHANNEL_ID = "StopsNotificationService"
        val ONGOING_NOTIFICATION_ID = 999
    }
}
