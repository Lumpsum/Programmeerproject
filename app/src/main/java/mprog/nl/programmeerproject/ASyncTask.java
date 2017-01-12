package mprog.nl.programmeerproject;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Rick on 1/11/2017.
 */

public class ASyncTask extends AsyncTask<String, String, StringBuilder> {
    String geocodeReq = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    String keyAPI = "AIzaSyCi9Oz8sVrpql3UA6N6xjjk-71YnxQbAtw";

    @Override
    protected StringBuilder doInBackground(String... params) {
        InputStream input;
        try {
            input = new URL(geocodeReq +
                    URLEncoder.encode(params[0], "UTF-8") + "+" +
                    URLEncoder.encode(params[1], "UTF-8") + "," +
                    URLEncoder.encode(params[2], "UTF-8") +
                    "&key=" + keyAPI
            ).openStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            StringBuilder result = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                result.append(line);
            }

            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
