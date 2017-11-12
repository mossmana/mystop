package net.killerandroid.heythatsmystop.notification

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
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

    @Test
    fun testIsNotificationEnabled() {
        assertThat(settings?.isNotificationEnabled("test location"), `is`(true))
        settings?.enableNotification("test location", false)
        assertThat(settings?.isNotificationEnabled("test location"), `is`(false))
    }

    @Test
    fun testGetStops() {
        settings?.enableNotification("test location 1", true)
        settings?.enableNotification("test location 2", false)
        settings?.enableNotification("test location 3", true)
        val stops = settings?.getStops()
        assertThat(stops?.size, `is`(3))
        var stop = stops?.toArray()!![0] as StopNotification
        assertThat(stop.name, `is`("test location 1"))
        stop = stops?.toArray()!![1] as StopNotification
        assertThat(stop.name, `is`("test location 2"))
        stop = stops?.toArray()!![2] as StopNotification
        assertThat(stop.name, `is`("test location 3"))
    }

    @Test
    fun testShouldShowNotification() {
        settings?.addNotification("test location", LatLng(45.508990, -122.764143));
        assertThat(settings?.shouldShowNotification(45.508859, -122.764573), `is`(true))
    }
}