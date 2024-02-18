package edu.cornell.resultsmanager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
    private List<TestResultRecord> results = new LinkedList<>();

    /**
     * Appends a test result to the list of results.
     *
     * @param className   The name of the test class.
     * @param value       The result value of the test.
     * @param secondsTaken The time taken for the test in seconds.
     */
    public void appendTestResult(String className, String value, int secondsTaken) {
        results.add(new TestResultRecord(className, value, secondsTaken));
    }

    /**
     * Converts the list of test results to JSON format.
     *
     * @return A JSON representation of the test results.
     */
    public String toJson() {
        Gson gson = new Gson();
        JsonObject jsonOutput = new JsonObject();

        for (TestResultRecord record : results) {
            JsonObject recordJson = new JsonObject();
            recordJson.addProperty("value", record.value());
            recordJson.addProperty("secondsTaken", record.secondsTaken());
            jsonOutput.add(record.className(), recordJson);
        }

        return gson.toJson(jsonOutput);
    }

    /**
     * Represents a single test result record.
     */
    public record TestResultRecord(String className, String value, int secondsTaken) {
    }
}
