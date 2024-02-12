package edu.cornell.resultsmanager;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
public class TestOutputSender {

    public static void sendResults(String json, String url) {
        try {
            URL postUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String postData = "results=" + java.net.URLEncoder.encode(json, "UTF-8");

            try (OutputStream os = connection.getOutputStream()) {
                os.write(postData.getBytes());
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                LOGGER.info("JSON sent successfully to: " + url);
            } else {
                LOGGER.error("Failed to send JSON. Response code: " + responseCode);
            }

            connection.disconnect();
        } catch (MalformedURLException e) {
            LOGGER.error("Invalid URL: " + url, e);
        } catch (Exception e) {
            LOGGER.error("Error while sending JSON", e);
        }
    }
}
