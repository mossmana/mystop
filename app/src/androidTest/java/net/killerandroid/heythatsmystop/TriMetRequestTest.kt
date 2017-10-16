package net.killerandroid.heythatsmystop

import android.support.test.runner.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TriMetRequestTest {
    @Test
    fun testBboxToString() {
        val swCoords = LatLng(-90.0, 53.5)
        val neCoords = LatLng(79.5, -80.75)
        val bounds = LatLngBounds(swCoords, neCoords)
        val bbox = TriMetRequest.Bbox(bounds)
        assertThat(bbox.toString(), `is`("-90.0,53.5,79.5,-80.75"))
    }

    @Test
    fun testBuildUrl() {
        val swCoords = LatLng(-70.33, 49.6)
        val neCoords = LatLng(15.0, -17.75)
        val bounds = LatLngBounds(swCoords, neCoords)
        val bbox = TriMetRequest.Bbox(bounds)
        val request = TriMetRequest(bbox)
        assertThat(request.url, `is`("https://developer.trimet.org/ws/V1/stops?appID=539B8725193DC0D7BC0B2A158&bbox=-70.33%2C49.6%2C15.0%2C-17.75&showRoutes=true"))
    }
}
