package net.killerandroid.heythatsmystop;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TriMetResponse {
    private static final String TAG = TriMetResponse.class.getSimpleName();

    public String response;

    public TriMetResponse(@NonNull String response) {
        this.response = response;
        //parse(response);
    }

    private void parse(String response) {
        // TODO
        InputStream stream = null;
        try {
            stream = new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8.name()));
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(stream, null);
            int eventType = parser.getEventType();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }

    }

    public interface Listener {

        void onResponse(TriMetResponse response);
        void onError(String error);
    }
}
