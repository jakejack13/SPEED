package edu.cornell.testoutputstream;

import lombok.NoArgsConstructor;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Custom deserializer for deserializing TestResultsRecord objects from byte arrays.
 */
@NoArgsConstructor
public class TestResultsRecordSerializer implements Serializer<TestResultsRecord> {

    /**
     * No configuration is needed.
     *
     * @param configs The configuration settings map.
     * @param isKey   Whether this is the key deserializer or not.
     */
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // No additional configuration needed
    }

    /**
     * Deserialize a byte array test result from worker into a TestResultsRecord object.
     *
     * @param topic The topic name.
     * @param data  The byte array representing the serialized TestResultsRecord object.
     * @return The deserialized TestResultsRecord object.
     */
    @Override
    public byte[] serialize(String topic, TestResultsRecord data) {
        if (data == null) {
            return null;
        }

        return data.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * No resources to release.
     */
    @Override
    public void close() {
        // No resources to release
    }
}