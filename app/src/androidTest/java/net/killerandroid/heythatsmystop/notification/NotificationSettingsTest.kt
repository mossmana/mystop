package net.killerandroid.heythatsmystop.notification

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationSettingsTest {

    var settings: NotificationSettings? = null

    @Before
    fun setupSettings() {
        settings = NotificationSettings(InstrumentationRegistry.getContext(), "test")
    }

    @Test
    fun testAddAndRemoveNotification() {
        val location = LatLng(12.0,-35.03)
        assertThat(settings?.isNotificationSet("test location"), `is`(false))
        settings?.addNotification("test location", location)
        assertThat(settings?.isNotificationSet("test location"), `is`(true))
        settings?.removeNotification("test location")
        assertThat(settings?.isNotificationSet("test location"), `is`(false))
    }

    @Test
    fun testAreNotificationsEnabled() {
        assertThat(settings?.areNotificationsEnabled(), `is`(false))
        settings?.enableNotifications(true)
        assertThat(settings?.areNotificationsEnabled(), `is`(true))
    }
}