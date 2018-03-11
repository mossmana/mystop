package net.killerandroid.mystop.trimet


import com.android.volley.Request
import com.android.volley.Response

/**
 * https://developer.trimet.org/ws_docs/stop_location_ws.shtml
 */
internal class TriMetRequest(bbox: net.killerandroid.mystop.trimet.TriMetRequest.Bbox) {

    @android.support.annotation.VisibleForTesting(otherwise = android.support.annotation.VisibleForTesting.PRIVATE)
    var url: String? = null

    init {
        buildUrl(bbox)
    }

    private fun buildUrl(bbox: net.killerandroid.mystop.trimet.TriMetRequest.Bbox) {
        val uri = android.net.Uri.Builder()
                .scheme("https")
                .authority(net.killerandroid.mystop.trimet.TriMetRequest.Companion.BASE_AUTHORITY)
                .appendPath("ws")
                .appendPath("V1")
                .appendPath("stops")
                .appendQueryParameter(net.killerandroid.mystop.trimet.TriMetRequest.Companion.APP_ID_PARAM, net.killerandroid.mystop.trimet.TriMetRequest.Companion.APP_ID)
                .appendQueryParameter(net.killerandroid.mystop.trimet.TriMetRequest.Companion.BBOX_PARAM, bbox.toString())
                .appendQueryParameter(net.killerandroid.mystop.trimet.TriMetRequest.Companion.SHOW_ROUTES_PARAM, "true")
                .build()
        url = uri.toString()
    }

    fun send(context: android.content.Context, listener: net.killerandroid.mystop.trimet.TriMetResponse.Listener) {
        val queue = com.android.volley.toolbox.Volley.newRequestQueue(context)
        val stringRequest = com.android.volley.toolbox.StringRequest(Request.Method.GET, url,
                Response.Listener<String> {
                    response ->
                    listener.onResponse(net.killerandroid.mystop.trimet.TriMetResponse(response))
                },
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
    (bounds: com.google.android.gms.maps.model.LatLngBounds) {
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
