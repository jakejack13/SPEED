package edu.cornell;

import edu.cornell.testconsumer.TestConsumer;
import java.time.Duration;
import java.util.Properties;
import java.util.Set;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

/** The runner that listens for test result updates from the workers via the Kafka cluster */
@Slf4j
public class KafkaConsumerRunner implements Runnable, AutoCloseable {

    /**
     * The Kafka consumer to receive test results from
     */
    private final @NonNull KafkaConsumer<String, String> consumer;

    /**
     * The test consumer to send test results to
     */
    private final @NonNull TestConsumer testConsumer;

    /**
     * Creates a new test consumer runner
     * @param kafkaAddress the address of the Kafka message bus
     * @param workerIds the list of workers to subscribe to on the message bus
     * @param testConsumer the test consumer to send test results to
     */
    KafkaConsumerRunner(@NonNull String kafkaAddress, @NonNull Set<String> workerIds,
            @NonNull TestConsumer testConsumer) {

        this.testConsumer = testConsumer;

        // create consumer configs
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaAddress);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "leaders");
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(workerIds);
    }

    @Override
    public void run() {
        LOGGER.info("Executing KafkaConsumerRunner");
        // poll for new data
        while(!testConsumer.isDone()){
            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records){
                LOGGER.info("Key: " + record.key() + ", Value: " + record.value());
                LOGGER.info("Partition: " + record.partition() + ", Offset:" + record.offset());
                testConsumer.processTestOutput(record.key(), record.value());
            }
        }
    }

    @Override
    public void close() {
        consumer.close();
    }
}
