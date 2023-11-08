package edu.cornell.testoutput;

import edu.cornell.Main;

/**
 * A factory for creating new TestOutputStreams
 */
public class TestOutputStreamFactory {

    /**
     * Creates and returns a new TestOutputStream
     * @param kafkaAddress the address of the Kafka message bus
     * @return a new TestOutputStream
     */
    public static TestOutputStream createTestOutputStream(String kafkaAddress) {
        if (Main.DEBUG_MODE) {
            return new PrintTestOutputStream();
        }
        return new KafkaTestOutputStream(kafkaAddress);
    }
}
