package edu.cornell.resultsmanager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.NoArgsConstructor;
import java.util.LinkedList;
import java.util.List;

/**
 * A utility class for parsing test results output and converting it to JSON format.
 */
@NoArgsConstructor
public class TestOutputParser {

    /**
     * List to store test result records.
     */
    private List<ParsedResultRecord> results = new LinkedList<>();

    /**
     * Appends a test result to the list of results.
     * @param className   The name of the test class.
     * @param value       The result value of the test.
     * @param nanoSecondsTaken The time taken for the test in nano seconds.
     */
    public void appendTestResult(String className, String value, int nanoSecondsTaken) {
        results.add(new ParsedResultRecord(className, value, nanoSecondsTaken));
    }

    /**
     * Converts the list of test results to JSON format.
     * @return A JSON representation of the test results.
     */
    public String toJson() {
        Gson gson = new Gson();
        JsonObject jsonOutput = new JsonObject();

        for (ParsedResultRecord record : results) {
            JsonObject recordJson = new JsonObject();
            recordJson.addProperty("value", record.value());
            recordJson.addProperty("nanoSecondsTaken", record.nanoSecondsTaken());
            jsonOutput.add(record.className(), recordJson);
        }

        return gson.toJson(jsonOutput);
    }

    /**
     * Represents a single test result record for parsing into JSON.
     * @param className the name of the test class ran
     * @param value the result of the test class
     * @param nanoSecondsTaken the execution time of the test class
     */
    public record ParsedResultRecord(String className, String value, int nanoSecondsTaken) {
    }
}
