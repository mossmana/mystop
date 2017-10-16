package net.killerandroid.heythatsmystop


import android.content.Context
import android.net.Uri
import android.support.annotation.VisibleForTesting
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.model.LatLngBounds

/**
 * https://developer.trimet.org/ws_docs/stop_location_ws.shtml
 */
internal class TriMetRequest(bbox: TriMetRequest.Bbox) {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var url: String? = null

    init {
        buildUrl(bbox)
    }

    private fun buildUrl(bbox: Bbox) {
        val uri = Uri.Builder()
                .scheme("https")
                .authority(BASE_AUTHORITY)
                .appendPath("ws")
                .appendPath("V1")
                .appendPath("stops")
                .appendQueryParameter(APP_ID_PARAM, APP_ID)
                .appendQueryParameter(BBOX_PARAM, bbox.toString())
                .appendQueryParameter(SHOW_ROUTES_PARAM, "true")
                .build()
        url = uri.toString()
    }

    fun send(context: Context, listener: TriMetResponse.Listener) {
        val queue = Volley.newRequestQueue(context)
        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> {
                    response -> listener.onResponse(TriMetResponse(response)) },
                Response.ErrorListener { error -> listener.onError(error.message) })
        queue.add(stringRequest)
    }

    /**
     * comma delimited list of longitude and latitude values
     */
    class Bbox
    /**
     * bbox arguments are lonmin, latmin, lonmax, latmax in decimal degrees.
     * These define the lower left and upper right corners of the bounding box.
     */
    (bounds: LatLngBounds) {
        private val latmin: Double
        private val lonmin: Double
        private val latmax: Double
        private val lonmax: Double

        init {
            this.latmin = bounds.southwest.latitude
            this.lonmin = bounds.southwest.longitude
            this.latmax = bounds.northeast.latitude
            this.lonmax = bounds.northeast.longitude
        }

        override fun toString(): String {
            return latmin.toString() + "," + lonmin + "," + latmax + "," + lonmax
        }
    }

    companion object {

        private val APP_ID = "539B8725193DC0D7BC0B2A158"
        private val BASE_AUTHORITY = "developer.trimet.org"
        private val APP_ID_PARAM = "appID"
        private val BBOX_PARAM = "bbox"
        private val SHOW_ROUTES_PARAM = "showRoutes"
    }
}
