package net.killerandroid.mystop.notification

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import net.killerandroid.mystop.MapsActivity
import java.util.*

class NotificationSettings(context: android.content.Context, name: String? = NAME) {

    var prefs: android.content.SharedPreferences? = null

    init {
        // TODO: Remove this Java interoperability workaround once Kotlin is used 100%
        if (name != null)
            prefs = context.getSharedPreferences(name, 0)
        else
            prefs = context.getSharedPreferences(NAME, 0)
    }

    fun addNotification(name: String, location: com.google.android.gms.maps.model.LatLng) {
        val edit = prefs!!.edit()
        edit.putString(buildNotificationKey(name), location.latitude.toString() + "," + location.longitude.toString())
        edit.putBoolean(buildNotificationEnabledKey(name), true)
        edit.apply()
    }

    fun removeNotification(name: String) {
        val edit = prefs!!.edit()
        edit.remove(buildNotificationKey(name))
        edit.remove(buildNotificationEnabledKey(name))
        edit.apply()
    }

    fun isNotificationSet(name: String): Boolean {
        return prefs!!.contains(buildNotificationKey(name))
    }

    fun enableNotification(name: String, enabled : Boolean) {
        val edit = prefs!!.edit()
        edit.putBoolean(buildNotificationEnabledKey(name), enabled)
        edit.apply()
    }

    fun isNotificationEnabled(name: String): Boolean {
        return prefs!!.getBoolean(buildNotificationEnabledKey(name), true)
    }

    fun enableNotifications(enable: Boolean) {
        val edit = prefs!!.edit()
        edit.putBoolean(NOTIFICATIONS_KEY, enable)
        edit.apply()
    }

    fun areNotificationsEnabled(): Boolean {
        return prefs!!.getBoolean(NOTIFICATIONS_KEY, true)
    }

    fun getStops() : TreeSet<StopNotification> {
        val stops = TreeSet<StopNotification>(NotificationComparator())
        for (key in prefs!!.all.keys) {
            if (key.startsWith(NOTIFICATION_ENABLED)) {
                val name = key.split("--")[1]
                val enabled = prefs!!.getBoolean(key, true)
                stops.add(StopNotification(name, enabled))
            }
        }
        return stops
    }

    fun shouldShowNotification(lat: Double, lng: Double): Boolean {
        if (!areNotificationsEnabled())
            return false

        for (key in prefs!!.all.keys) {
            if (key.startsWith(NOTIFICATION) && !key.startsWith(NOTIFICATIONS_KEY)
                    && !key.startsWith(NOTIFICATION_ENABLED) &&
                    prefs!!.getBoolean(key.replace("notification", "notification.enabled"), false)) {
                val notificationVal = prefs!!.getString(key, "").split(",")
                val notificationLocation: LatLng?
                if (notificationVal.size == 2) {
                    notificationLocation =
                            LatLng(notificationVal[0].toDouble(), notificationVal[1].toDouble())
                } else {
                    notificationLocation = MapsActivity.defaultLocation
                }
                val result = FloatArray(3)
                Location.distanceBetween(lat, lng,
                        notificationLocation!!.latitude, notificationLocation!!.longitude, result)
                if (result[0] <= DISTANCE_IN_METERS)
                    return true
            }
        }
        return false
    }

    private fun buildNotificationKey(name: String): String {
        return NOTIFICATION_KEY.format(name)
    }

    private fun buildNotificationEnabledKey(name: String): String {
        return NOTIFICATION_ENABLED_KEY.format(name)
    }

    class NotificationComparator: Comparator<StopNotification> {

        override fun compare(notification1: StopNotification?, notification2: StopNotification?): Int {
            return notification1?.name!!.compareTo(notification2?.name!!)
        }
    }

    fun clear() {
        val edit = prefs!!.edit();
        edit.clear().commit();
    }

    companion object {

        private val NAME = "net.killerandroid.mystop.prefs"
        private val NOTIFICATIONS_KEY = "net.killerandroid.mystop.prefs.notifications"
        private val NOTIFICATION = "net.killerandroid.mystop.prefs.notification"
        private val NOTIFICATION_KEY = NOTIFICATION + "--%s"
        private val NOTIFICATION_ENABLED = "net.killerandroid.mystop.prefs.notification.enabled"
        private val NOTIFICATION_ENABLED_KEY = NOTIFICATION_ENABLED + "--%s"
        private val DISTANCE_IN_METERS = 50
    }
}