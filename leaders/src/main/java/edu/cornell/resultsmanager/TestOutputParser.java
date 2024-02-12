package edu.cornell.resultsmanager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.*;
import org.bouncycastle.util.test.TestResult;

import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
public class TestOutputParser {

    private List<TestResultRecord> results = new LinkedList<>();

    public void appendTestResult(String className, String value, int secondTaken) {
        results.add(new TestResultRecord(className, value, secondTaken));
    }

    public String toJson() {
        Gson gson = new Gson();
        JsonObject jsonOutput = new JsonObject();

        for (TestResultRecord record : results) {
            JsonObject recordJson = new JsonObject();
            recordJson.addProperty("value", record.getValue());
            recordJson.addProperty("secondsTaken", record.getSecondsTaken());
            jsonOutput.add(record.getClassName(), recordJson);
        }

        return gson.toJson(jsonOutput);
    }

    @AllArgsConstructor
    @Getter
    private class TestResultRecord {
        private final String className;
        private final String value;
        private final int secondsTaken;
    }
}
