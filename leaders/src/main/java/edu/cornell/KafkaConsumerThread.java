package edu.cornell;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import lombok.NonNull;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

/** The thread that listens for updates from the workers via the Kafka cluster */
public class KafkaConsumerThread implements Runnable {

    private final KafkaConsumer<String, String> consumer;

    public KafkaConsumerThread(@NonNull String kafkaAddress, @NonNull List<String> workerIds) {


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
    // poll for new data
        while(true){
            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records){
                System.out.println("Key: " + record.key() + ", Value: " + record.value());
                System.out.println("Partition: " + record.partition() + ", Offset:" + record.offset());
            }
        }
    }
}
