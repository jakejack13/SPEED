package edu.cornell.testoutputstream;

import edu.cornell.Main;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * A class allowing the test runner to log its results with clients through a Kafka message bus.
 */
@Slf4j
class KafkaTestOutputStream implements TestOutputStream {

    /**
     * The Kafka producer for logging messages.
     */
    private final @NonNull Producer<String, TestResultsRecord> producer;
    /**
     * The topic to log Kafka messages to, which is the name of the container.
     */
    private @NonNull String topicName;

    /**
     * Current address to the kafka service <b>within the kafka network</b>.
     */
    private static final @NonNull String KAFKA_ADDRESS = System.getenv(Main.ENV_KAFKA_ADDRESS);

    /**
     * Creates a new TestOutputStream.
     */
    KafkaTestOutputStream() {
        //Assign topicName to hostname
        try (BufferedReader inputStream = new BufferedReader(
                new InputStreamReader(
                        new ProcessBuilder("hostname").start().getInputStream(), 
                            StandardCharsets.UTF_8))) {
            topicName = inputStream.readLine();
        } catch (IOException e) {
            topicName = "error";
            LOGGER.error("Error getting hostname from container", e);
            throw new RuntimeException(e);
        }

        LOGGER.info("topicName = " + topicName + " kafkaAddress = " + KAFKA_ADDRESS);

        // create instance for properties to access producer configs
        Properties properties = new Properties();
        //Assign localhost id
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_ADDRESS);
        //Set acknowledgements for producer requests.
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        //If the request fails, the producer can automatically retry,
        properties.setProperty(ProducerConfig.RETRIES_CONFIG, "0");
        //Specify buffer size in config
        properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
        //Reduce the no of requests less than 0
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "1");
        //The buffer.memory controls the total amount of memory available to the producer 
        //for buffering.
        properties.setProperty(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                TestResultsRecordSerializer.class.getName());

        producer = new KafkaProducer<>(properties);
    }

    /**
     * Sends a test result to a Kafka topic.
     *
     * @param testName     The name of the test.
     * @param result       The test result.
     * @param elapsedTime  The elapsed time of the test.
     */
    @Override
    public void sendTestResult(@NonNull String testName, @NonNull TestResult result, 
            int elapsedTime) {
        LOGGER.info(testName + ":" + result);
        LOGGER.debug("Sent");
        producer.send(new ProducerRecord<>(topicName,
                    testName, new TestResultsRecord(result.toString(), elapsedTime)));
    }

    @Override
    public void done() {
        producer.send(new ProducerRecord<>(topicName,
                topicName, new TestResultsRecord("DONE", 0)));
    }

    @Override
    public void close() {
        producer.close();
    }
}
