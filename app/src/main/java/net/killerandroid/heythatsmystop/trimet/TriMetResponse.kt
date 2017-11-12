package net.killerandroid.heythatsmystop.trimet

import android.util.Log
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.IOException
import java.io.InputStream
import java.io.StringReader
import java.util.*

/**
 * <resultSet xmlns="urn:trimet:arrivals" queryTime="1508036914119">
 *      <location desc="SW Barnes &amp; 84th" dir="Eastbound" lat="45.5089649365313" lng="-122.763858362376" locid="12960">
 *          <route desc="20-Burnside/Stark" route="20" type="B" />
 *      </location>
 *      ...
 * </resultSet>
 */
class TriMetResponse(var response: String)
{
    var stops: MutableList<StopLocation>? = null
        private set

    init {
        parse(response)
    }

    private fun parse(response: String) {
        stops = ArrayList<StopLocation>()
        val stream: InputStream? = null
        try {
            val parser = Xml.newPullParser()
            parser.setInput(StringReader(response))
            var eventType = parser.eventType
            var done = false
            var stop: StopLocation? = null
            var route: StopLocation.Route?
            while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                val name: String
                when (eventType) {
                    XmlPullParser.START_DOCUMENT -> {
                    }
                    XmlPullParser.START_TAG -> {
                        name = parser.name
                        if (name.equals(LOCATION, ignoreCase = true)) {
                            stop = StopLocation()
                            stop.desc = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, DESC)
                            stop.dir = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, DIR)
                            stop.lat = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, LAT)
                            stop.lng = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, LNG)
                            stop.locid = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, LOCID)
                        } else if (stop != null) {
                            if (name.equals(ROUTE, ignoreCase = true)) {
                                route = StopLocation.Route()
                                stop.route = route
                                stop.route!!.desc =
                                        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, DESC)
                                stop.route!!.route =
                                        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, ROUTE)
                                stop.route!!.type =
                                        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, TYPE)
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        name = parser.name
                        if (name.equals(LOCATION, ignoreCase = true) && stop != null) {
                           stops!!.add(stop)
                        } else if (name.equals(RESULT_SET)) {
                           done = true;
                        }
                    }
                }
                eventType = parser.next();
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        } finally {
            if (stream != null) {
                try {
                    stream.close()
                } catch (e: IOException) {
                    Log.e(TAG, e.message)
                }

            }
        }
    }

    internal interface Listener {

        fun onResponse(response: TriMetResponse)
        fun onError(error: String?)
    }

    companion object {
        private val TAG = TriMetResponse::class.java.getSimpleName()

        private val RESULT_SET = "resultSet"
        private val LOCATION = "location"
        private val ROUTE = "route"
        private val DESC = "desc"
        private val DIR = "dir"
        private val LAT = "lat"
        private val LNG = "lng"
        private val LOCID = "locid"
        private val TYPE = "type"
    }
}
