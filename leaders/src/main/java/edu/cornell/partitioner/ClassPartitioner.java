package edu.cornell.partitioner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.cornell.Main;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The {@code ClassPartitioner} class provides functionality to partition test classes
 * based on responses from a specified endpoint. It builds a JSON POST request from
 * a list of test classes and configuration data pulled from environment variables.
 */
@Slf4j
public class ClassPartitioner {

    /**
     * Sends a POST request to the specified URL to partition test classes.
     * The request includes repository details and the test classes to be partitioned.
     *
     * @param url The endpoint URL where the request is sent.
     * @param testsList A list of test class names that need to be partitioned.
     * @return A list of sets, where each set contains names of
     * test classes that are grouped together.
     *         Returns {@code null} in case of any errors.
     */
    public static List<Set<String>> partitionClasses(String url, List<String> testsList) {
        try {
            // Create HttpClient
            HttpClient client = HttpClient.newHttpClient();
            Gson gson = new Gson();

            // Build JSON body using GSON
            JsonObject root = new JsonObject();

            // URL of the git repository
            root.addProperty("url", System.getenv(Main.ENV_REPO_URL));

            // Branch of the git repository
            root.addProperty("branch", System.getenv(Main.ENV_REPO_BRANCH));

            // Number of workers
            root.addProperty("num_workers",
                    Integer.parseInt(System.getenv(Main.ENV_NUM_WORKERS)));

            JsonArray testClasses = new JsonArray();
            for (String test : testsList) {
                JsonObject testClass = new JsonObject();
                testClass.addProperty("name", test);
                testClasses.add(testClass);
            }
            root.add("testclasses", testClasses);

            // Convert JsonObject to String for the POST request body
            String jsonBody = gson.toJson(root);

            // Build HttpRequest with JSON body
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            // Send HttpRequest and receive HttpResponse
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            // Parse JSON response to extract partitions
            JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
            JsonArray partitions = jsonResponse.getAsJsonArray("partitions");
            List<Set<String>> partitionsList = new ArrayList<>();

            for (int i = 0; i < partitions.size(); i++) {
                JsonObject partition = partitions.get(i).getAsJsonObject();
                JsonArray testClassesArray = partition.getAsJsonArray("testclasses");
                Set<String> testSet = new HashSet<>();

                for (int j = 0; j < testClassesArray.size(); j++) {
                    JsonObject testClassObject = testClassesArray.get(j).getAsJsonObject();
                    testSet.add(testClassObject.get("name").getAsString());
                }

                partitionsList.add(testSet);
            }

            return partitionsList;

        } catch (IOException | InterruptedException e) {
            LOGGER.error(e.toString());
            return null; // or handle error appropriately
        }
    }
}
