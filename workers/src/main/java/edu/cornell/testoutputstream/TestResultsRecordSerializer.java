package edu.cornell.testoutputstream;

import lombok.NoArgsConstructor;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

@NoArgsConstructor
public class TestResultsRecordSerializer implements Serializer<TestResultsRecord> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // No additional configuration needed
    }

    @Override
    public byte[] serialize(String topic, TestResultsRecord data) {
        if (data == null) {
            return null;
        }
        // Assuming TestResultsRecord has a toString() method that provides the desired string representation
        return data.toString().getBytes();
    }

    @Override
    public void close() {
        // No resources to release
    }
}