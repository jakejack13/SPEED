package edu.cornell.status;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * A utility class for updating deployment status by sending HTTP POST requests to a specified URL.
 */
public class StatusUpdater {

    /**
     * Sends an HTTP POST request to update the deployment status.
     *
     * @param url    The URL to which the request will be sent.
     * @param status The deployment status to be updated.
     */
    public static void updateDeploymentStatus(String url, DeploymentStatus status) {
        try {
            // Construct the URL
            URI uri = new URI(url);

            // Open a connection
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Create JSON payload
            String payload = "{\"status\": \"" + status.toString() + "\"}";

            // Set content type
            connection.setRequestProperty("Content-Type", "application/json");

            // Write payload to the connection's output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get response code (optional)
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Disconnect
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
