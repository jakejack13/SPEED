package edu.cornell;

import edu.cornell.resultsmanager.TestOutputParser;
import edu.cornell.resultsmanager.TestOutputSender;
import edu.cornell.testconsumer.TestConsumer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.time.Duration;
import java.util.Properties;
import java.util.Set;

/** 
 * The runner that listens for test result updates from the workers via the Kafka cluster.
 */
@Slf4j
public class KafkaConsumerRunner implements Runnable, AutoCloseable {

    /**
     * The Kafka consumer to receive test results from.
     */
    private final @NonNull KafkaConsumer<String, TestResultsRecord> consumer;

    /**
     * The test consumer to send test results to.
     */
    private final @NonNull TestConsumer testConsumer;

    /**
     * The deployment ID that this leader belongs to.
     */
    private final @NonNull Integer deploymentID;

    /**
     * Creates a new test consumer runner.
     * @param kafkaAddress the address of the Kafka message bus
     * @param workerIds the list of workers to subscribe to on the message bus
     * @param testConsumer the test consumer to send test results to
     * @param deploymentID the deployment ID that this leader belongs to
     */
    KafkaConsumerRunner(@NonNull String kafkaAddress, @NonNull Set<String> workerIds,
            @NonNull TestConsumer testConsumer, @NonNull Integer deploymentID) {

        this.testConsumer = testConsumer;
        this.deploymentID = deploymentID;

        // create consumer configs
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaAddress);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, 
            StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, 
            TestResultsRecordDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "leaders");
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(workerIds);
    }

    /**
     * Runs the KafkaConsumerRunner, continuously polling for new data from the Kafka topic.
     * Upon receiving new records, processes them using a TestOutputParser, 
     * logs the key-value pairs, and sends the results.
     */
    @Override
    public void run() {
        LOGGER.info("Executing KafkaConsumerRunner");
        TestOutputParser testOutputParser = new TestOutputParser();
        // poll for new data
        while (!testConsumer.isDone()) {
            ConsumerRecords<String, TestResultsRecord> records =
                    consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, TestResultsRecord> record : records) {
                testOutputParser.appendTestResult(record.key(), record.value().result(), 
                    record.value().elapsedTime());
                LOGGER.info("Key: {}, Value: {}", record.key(), record.value().toString());

                LOGGER.info("Partition: {}, Offset:{}", record.partition(), record.offset());
                testConsumer.processTestOutput(record.key(), record.value().result());
            }
        }

        TestOutputSender.sendResults(testOutputParser.toJson(),
            ENDPOINTS.BASE_ENDPOINT + ENDPOINTS.FIRST_PORT +
                    ENDPOINTS.ADD_RESULTS_ROUTE + "/" + deploymentID);
    }

    @Override
    public void close() {
        consumer.close();
    }
}
