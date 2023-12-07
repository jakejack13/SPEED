package edu.cornell;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * The main leader application
 */
@Slf4j
public class Main {

    /**
     * Debug mode flag, used for turning on debug mode
     */
    public static final boolean DEBUG_MODE = System.getProperty("speed.debug") != null &&
            "true".equalsIgnoreCase(System.getProperty("speed.debug"));

    public static void main(String[] args) {
        String kafkaAddress = System.getenv("SPEED_KAFKA_ADDRESS");
        List<String> workerIds = List.of("dhcp-vl2041-39795.eduroam.cornell.edu"); // FIXME: Replace
        KafkaConsumerThread consumer = new KafkaConsumerThread(kafkaAddress, workerIds);
        consumer.run();
    }
}
