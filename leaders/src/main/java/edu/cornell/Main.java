package edu.cornell;

import edu.cornell.testconsumer.PrintTestConsumer;
import edu.cornell.testconsumer.TestConsumer;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * The main leader application
 * Environment variables:
 * SPEED_REPO_URL: the url of the repository to clone
 * SPEED_REPO_BRANCH: the branch of the repository to clone
 * SPEED_KAFKA_ADDRESS: the address of the message bus to send test results to
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
        List<String> workerIds;
        if (DEBUG_MODE) {
             workerIds = List.of("localhost");
        } else {
            workerIds = List.of(); // FIXME: Replace
        }
        Map<String, Integer> testMethods = Map.of("TestClass", 1); // FIXME: Replace
        TestConsumer testConsumer = new PrintTestConsumer(testMethods);
        try (KafkaConsumerRunner consumer =
                new KafkaConsumerRunner(kafkaAddress, workerIds, testConsumer)) {
            consumer.run();
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage());
            System.exit(1);
        }
    }
}
