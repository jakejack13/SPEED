package edu.cornell;

import lombok.NoArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@NoArgsConstructor
public class TestResultsRecordDeserializer implements Deserializer<TestResultsRecord> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // No additional configuration needed
    }

    @Override
    public TestResultsRecord deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }

        String dataString = new String(data, StandardCharsets.UTF_8);
        String result = dataString.substring(dataString.indexOf("RESULT:") + 7, dataString.indexOf(";TIME_TAKEN:"));
        Integer elapsedTime = Integer.parseInt(dataString.substring(dataString.indexOf(";TIME_TAKEN:") + 12));
        // Construct and return a new TestResultsRecord object
        return new TestResultsRecord(result, elapsedTime);
    }

    @Override
    public void close() {
        // No resources to release
    }
}