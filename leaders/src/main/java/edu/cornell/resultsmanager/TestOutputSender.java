package edu.cornell.resultsmanager;

import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;

/**
 * A utility class for sending test results JSON to a specified URL via HTTP POST.
 */
@Slf4j
public class TestOutputSender {

    /**
     * Sends the test results JSON to the specified URL via HTTP POST.
     *
     * @param json The JSON string representing the test results.
     * @param url  The URL to which the JSON should be sent.
     */
    public static void sendResults(String json, String url) {
        try {
            URI postURI = new URI(url);
            HttpURLConnection connection = (HttpURLConnection) postURI.toURL().openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            String postData = "{\"results\":" + json + "}";

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
