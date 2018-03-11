package net.killerandroid.mystop.trimet

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat

@org.junit.runner.RunWith(android.support.test.runner.AndroidJUnit4::class)
class TriMetResponseTest {

    @org.junit.Test
    fun testParse() {
        val response = net.killerandroid.mystop.trimet.TriMetResponse(TEST_XML)
        assertThat(response.stops!!.size, `is`(6))
        assertThat(response.stops!!.get(2).desc, `is`("8200 Block SW Barnes (Art School)"))
        assertThat(response.stops!!.get(4).route!!.route, `is`("20"))
    }

    companion object {
        private val TEST_XML =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<resultSet xmlns=\"urn:trimet:arrivals\" queryTime=\"1508036914119\">\n" +
                        "    <location desc=\"SW Barnes &amp; 84th\" dir=\"Eastbound\" lat=\"45.5089649365313\" lng=\"-122.763858362376\" locid=\"12960\">\n" +
                        "        <route desc=\"20-Burnside/Stark\" route=\"20\" type=\"B\" />\n" +
                        "    </location>\n" +
                        "    <location desc=\"8200 Block SW Barnes (Art School)\" dir=\"Eastbound\" lat=\"45.5098659386311\" lng=\"-122.761710210069\" locid=\"230\">\n" +
                        "        <route desc=\"20-Burnside/Stark\" route=\"20\" type=\"B\" />\n" +
                        "    </location>\n" +
                        "    <location desc=\"8200 Block SW Barnes (Art School)\" dir=\"Westbound\" lat=\"45.5104199641404\" lng=\"-122.761307525206\" locid=\"231\">\n" +
                        "        <route desc=\"20-Burnside/Stark\" route=\"20\" type=\"B\" />\n" +
                        "    </location>\n" +
                        "    <location desc=\"8800 Block SW Barnes (Gabel School)\" dir=\"Westbound\" lat=\"45.5087160754909\" lng=\"-122.76792543282\" locid=\"235\">\n" +
                        "        <route desc=\"20-Burnside/Stark\" route=\"20\" type=\"B\" />\n" +
                        "    </location>\n" +
                        "    <location desc=\"SW Barnes &amp; 84th\" dir=\"Westbound\" lat=\"45.5090348779128\" lng=\"-122.764157179702\" locid=\"253\">\n" +
                        "        <route desc=\"20-Burnside/Stark\" route=\"20\" type=\"B\" />\n" +
                        "    </location>\n" +
                        "    <location desc=\"SW Barnes &amp; 88th\" dir=\"Eastbound\" lat=\"45.5084999189703\" lng=\"-122.767549036093\" locid=\"255\">\n" +
                        "        <route desc=\"20-Burnside/Stark\" route=\"20\" type=\"B\" />\n" +
                        "    </location>\n" +
                        "</resultSet>"
    }
}
