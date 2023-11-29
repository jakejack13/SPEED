package edu.cornell.testoutput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import java.util.Properties;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

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
        try (BufferedReader inputStream = new BufferedReader(
                new InputStreamReader(Runtime.getRuntime().exec("hostname").getInputStream()))) {
            topicName = inputStream.readLine();
        } catch (IOException e) {
            topicName = "error";
            LOGGER.error("Error getting hostname from container");
            System.exit(1);
        }

        // create instance for properties to access producer configs
        Properties props = new Properties();
        //Assign localhost id
        props.put("bootstrap.servers", kafkaAddress);
        //Set acknowledgements for producer requests.
        props.put("acks", "all");
        //If the request fails, the producer can automatically retry,
        props.put("retries", 0);
        //Specify buffer size in config
        props.put("batch.size", 16384);
        //Reduce the no of requests less than 0
        props.put("linger.ms", 1);
        //The buffer.memory controls the total amount of memory available to the producer for buffering.
        props.put("buffer.memory", 33554432);
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);
    }

    @Override
    public void sendTestResult(@NonNull String testClassName, @NonNull String testMethodName,
            @NonNull TestResult result) {
        producer.send(new ProducerRecord<>(topicName,
                    testClassName + ":" + testMethodName, result.toString()));
    }

    @Override
    public void close() {
        producer.close();
    }
}
