package net.killerandroid.heythatsmystop;

/**
 * <?xml version="1.0" encoding="UTF-8"?>
 * <resultSet xmlns="urn:trimet:arrivals" queryTime="1508033692950">
 *      <location desc="SW Barnes &amp; 84th" dir="Eastbound" lat="45.5089649365313" lng="-122.763858362376" locid="12960">
 *          <route desc="20-Burnside/Stark" route="20" type="B" />
 *      </location>
 * </resultSet>
 */
public class StopLocation {
    public String desc;
    public String dir;
    public String lat;
    public String lng;
    public String locid;

    public static class Route {
        public String desc;
        public String route;
        public String type;
    }
}
