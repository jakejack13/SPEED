package edu.cornell;

import lombok.NoArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Deserializes byte arrays into TestResultsRecord objects.
 */
@NoArgsConstructor
public class TestResultsRecordDeserializer implements Deserializer<TestResultsRecord> {

    /**
     * Nothing to configure.
     *
     * @param configs The configuration settings map.
     * @param isKey   Whether this is the key deserializer or not.
     */
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // No additional configuration needed
    }

    /**
     * Deserializes a byte array into a TestResultsRecord object.
     *
     * @param topic The topic name.
     * @param data  The byte array representing the serialized TestResultsRecord object.
     * @return The deserialized TestResultsRecord object, or null if the data is null.
     */
    @Override
    public TestResultsRecord deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }

        String dataString = new String(data, StandardCharsets.UTF_8);
        String result = dataString.substring(dataString.indexOf("RESULT:") + 7, 
            dataString.indexOf(";TIME_TAKEN:"));
        Integer elapsedTime = Integer.parseInt(dataString.substring(
            dataString.indexOf(";TIME_TAKEN:") + 12));
        // Construct and return a new TestResultsRecord object
        return new TestResultsRecord(result, elapsedTime);
    }

    /**
     * No resources to release.
     */
    @Override
    public void close() {
        // No resources to release
    }
}
