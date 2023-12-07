package edu.cornell.testoutputstream;

import edu.cornell.Main;
import lombok.NonNull;

/**
 * A factory for creating new TestOutputStreams
 */
public class TestOutputStreamFactory {

    /**
     * Creates and returns a new TestOutputStream
     * @param kafkaAddress the address of the Kafka message bus
     * @return a new TestOutputStream
     */
    public static @NonNull TestOutputStream createTestOutputStream(@NonNull String kafkaAddress) {
        return new KafkaTestOutputStream(kafkaAddress);
    }
}
