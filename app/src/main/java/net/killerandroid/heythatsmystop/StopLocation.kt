package net.killerandroid.heythatsmystop

/**
 *
 * <resultSet xmlns="urn:trimet:arrivals" queryTime="1508033692950">
 *    <location desc="SW Barnes &amp; 84th" dir="Eastbound" lat="45.5089649365313" lng="-122.763858362376" locid="12960">&amp;
 *       <route desc="20-Burnside/Stark" route="20" type="B"></route>
 *    </location>
 * </resultSet>
 */
class StopLocation {
    var desc: String? = null
    var dir: String? = null
    var lat: String? = null
    var lng: String? = null
    var locid: String? = null
    var route: Route? = null

    class Route {
        var desc: String? = null
        var route: String? = null
        var type: String? = null
    }
}
