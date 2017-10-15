package net.killerandroid.heythatsmystop;

import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class TriMetRequestTest {
    @Test
    public void testBboxToString() throws Exception {
        LatLng swCoords = new LatLng(-90.0, 53.5);
        LatLng neCoords = new LatLng(79.5, -80.75);
        LatLngBounds bounds = new LatLngBounds(swCoords, neCoords);
        TriMetRequest.Bbox bbox = new TriMetRequest.Bbox(bounds);
        assertThat(bbox.toString(), is("-90.0,53.5,79.5,-80.75"));
    }

    @Test
    public void testBuildUrl() {
        LatLng swCoords = new LatLng(-70.33, 49.6);
        LatLng neCoords = new LatLng(15.0, -17.75);
        LatLngBounds bounds = new LatLngBounds(swCoords, neCoords);
        TriMetRequest.Bbox bbox = new TriMetRequest.Bbox(bounds);
        TriMetRequest request = new TriMetRequest(bbox);
        assertThat(request.url, is("https://developer.trimet.org/ws/V1/stops?appID=539B8725193DC0D7BC0B2A158&bbox=-70.33%2C49.6%2C15.0%2C-17.75&showRoutes=true"));
    }
}
