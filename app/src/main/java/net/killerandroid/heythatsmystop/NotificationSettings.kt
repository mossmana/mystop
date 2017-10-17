package net.killerandroid.heythatsmystop

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.maps.model.LatLng

class NotificationSettings(context: Context, name: String? = NAME) {

    var prefs: SharedPreferences? = null

    init {
        // TODO: Remove this Java interoperability workaround once Kotlin is used 100%
        if (name != null)
            prefs = context.getSharedPreferences(name, 0)
        else
            prefs = context.getSharedPreferences(NAME, 0)
    }

    fun addNotification(name: String, location: LatLng) {
        val edit = prefs!!.edit();
        edit.putString(name, location.latitude.toString() + "," + location.longitude.toString())
        edit.apply()
    }

    fun removeNotification(name: String) {
        val edit = prefs!!.edit()
        edit.remove(name)
        edit.apply()
    }

    fun isNotificationSet(name: String) : Boolean {
        return prefs!!.contains(name!!)
    }

    fun enableNotifications(enable: Boolean) {
        val edit = prefs!!.edit()
        edit.putBoolean(NOTIFICATIONS, enable)
        edit.apply()
    }

    fun areNotificationsEnabled() : Boolean {
        return prefs!!.getBoolean(NOTIFICATIONS, false)
    }

    companion object {

        private val NAME = "net.killerandroid.heythatsmystop.prefs"
        private val NOTIFICATIONS = "net.killerandroid.heythatsmystop.prefs.notifications"
    }
}