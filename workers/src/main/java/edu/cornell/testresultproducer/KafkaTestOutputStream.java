package edu.cornell.testresultproducer;

import edu.cornell.Main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import java.util.Properties;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

/**
 * A class allowing the test runner to log its results with clients through a Kafka message bus
 */
@Slf4j
public class KafkaTestOutputStream implements TestOutputStream {

    /**
     * The Kafka producer for logging messages
     */
    private final @NonNull Producer<String,String> producer;
    /**
     * The topic to log Kafka messages to, which is the name of the container
     */
    private @NonNull String topicName;

    /**
     * Creates a new TestOutputStream
     * @param kafkaAddress the address of the Kafka cluster
     */
    public KafkaTestOutputStream(@NonNull String kafkaAddress) {
        //Assign topicName to hostname
        if (Main.DEBUG_MODE) {
            topicName = "localhost";
        } else {
            try (BufferedReader inputStream = new BufferedReader(
                    new InputStreamReader(
                            Runtime.getRuntime().exec("hostname").getInputStream()))) {
                topicName = inputStream.readLine();
            } catch (IOException e) {
                topicName = "error";
                LOGGER.error("Error getting hostname from container");
                System.exit(1);
            }
        }

        // create instance for properties to access producer configs
        Properties properties = new Properties();
        //Assign localhost id
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaAddress);
        //Set acknowledgements for producer requests.
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        //If the request fails, the producer can automatically retry,
        properties.setProperty(ProducerConfig.RETRIES_CONFIG, "0");
        //Specify buffer size in config
        properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
        //Reduce the no of requests less than 0
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "1");
        //The buffer.memory controls the total amount of memory available to the producer for buffering.
        properties.setProperty(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        producer = new KafkaProducer<>(properties);
    }

    @Override
    public void sendTestResult(@NonNull String testClassName, @NonNull String testMethodName,
            @NonNull TestResult result) {
        LOGGER.info(testClassName + ":" + testMethodName + ";" + result);
        producer.send(new ProducerRecord<>(topicName,
                    testClassName + ":" + testMethodName, result.toString()));
    }

    @Override
    public void close() {
        producer.close();
    }
}
