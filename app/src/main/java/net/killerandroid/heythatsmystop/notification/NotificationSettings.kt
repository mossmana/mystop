package net.killerandroid.heythatsmystop.notification

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
        val edit = prefs!!.edit();
        edit.putString(buildNotificationKey(name), location.latitude.toString() + "," + location.longitude.toString())
        edit.apply()
    }

    fun removeNotification(name: String) {
        val edit = prefs!!.edit()
        edit.remove(buildNotificationKey(name))
        edit.apply()
    }

    fun isNotificationSet(name: String): Boolean {
        return prefs!!.contains(buildNotificationKey(name!!))
    }

    fun enableNotification(name: String, enabled : Boolean) {
        val edit = prefs!!.edit()
        edit.putBoolean(buildNotificationEnabledKey(name), enabled);
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
        return prefs!!.getBoolean(NOTIFICATIONS_KEY, false)
    }

    fun getStops() : TreeSet<StopNotification> {
        val stops = TreeSet<StopNotification>(NotificationComparator())
        for (key in prefs!!.all.keys) {
            if (key.startsWith(NOTIFICATION_ENABLED)) {
                val name = key.split("-")[1]
                val enabled = prefs!!.getBoolean(key, false)
                stops.add(StopNotification(name, enabled))
            }
        }
        return stops
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

    companion object {

        private val NAME = "net.killerandroid.heythatsmystop.prefs"
        private val NOTIFICATIONS_KEY = "net.killerandroid.heythatsmystop.prefs.notifications"
        private val NOTIFICATION_KEY = "net.killerandroid.heythatsmystop.prefs.notification-%s"
        private val NOTIFICATION_ENABLED = "net.killerandroid.heythatsmystop.prefs.notification.enabled"
        private val NOTIFICATION_ENABLED_KEY = NOTIFICATION_ENABLED + "-%s"
    }
}