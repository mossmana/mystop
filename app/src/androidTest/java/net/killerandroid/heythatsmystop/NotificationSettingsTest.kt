package net.killerandroid.heythatsmystop

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationSettingsTest {
    @Test
    fun testAddAndRemoveNotification() {
        val settings = NotificationSettings(InstrumentationRegistry.getContext(), "test")
        val location = LatLng(12.0,-35.03)
        assertThat(settings.isNotificationSet("test location"), `is`(false))
        settings.addNotification("test location", location)
        assertThat(settings.isNotificationSet("test location"), `is`(true))
        settings.removeNotification("test location")
        assertThat(settings.isNotificationSet("test location"), `is`(false))
    }
}