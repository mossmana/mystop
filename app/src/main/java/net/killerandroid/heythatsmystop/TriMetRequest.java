package net.killerandroid.heythatsmystop;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.VisibleForTesting;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * https://developer.trimet.org/ws_docs/stop_location_ws.shtml
 */
class TriMetRequest {

    private static final String APP_ID = "539B8725193DC0D7BC0B2A158";
    private static final String BASE_AUTHORITY = "developer.trimet.org";
    private static final String APP_ID_PARAM = "appID";
    private static final String BBOX_PARAM = "bbox";
    private static final String SHOW_ROUTES_PARAM = "showRoutes";

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    String url;

    public TriMetRequest(Bbox bbox) {
        buildUrl(bbox);
    }

    private void buildUrl(Bbox bbox) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority(BASE_AUTHORITY)
                .appendPath("ws")
                .appendPath("V1")
                .appendPath("stops")
                .appendQueryParameter(APP_ID_PARAM, APP_ID)
                .appendQueryParameter(BBOX_PARAM, bbox.toString())
                .appendQueryParameter(SHOW_ROUTES_PARAM, "true")
                .build();
        url = uri.toString();
    }

    public void send(Context context, final TriMetResponse.Listener listener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        listener.onResponse(new TriMetResponse(response));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error.getMessage());
            }
        });
        queue.add(stringRequest);
    }

    /**
     * comma delimited list of longitude and latitude values
     */
    public static class Bbox {
        private final double latmin;
        private final double lonmin;
        private final double latmax;
        private final double lonmax;

        /**
         * bbox arguments are lonmin, latmin, lonmax, latmax in decimal degrees.
         * These define the lower left and upper right corners of the bounding box.
         */
        public Bbox(LatLngBounds bounds)
        {
            this.latmin = bounds.southwest.latitude;
            this.lonmin = bounds.southwest.longitude;
            this.latmax = bounds.northeast.latitude;
            this.lonmax = bounds.northeast.longitude;
        }

        @Override
        public String toString() {
            return latmin + "," + lonmin + "," + latmax + "," + lonmax;
        }
    }
}
