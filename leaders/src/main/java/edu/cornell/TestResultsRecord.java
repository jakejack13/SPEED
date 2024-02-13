package edu.cornell;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Data
@AllArgsConstructor
public class TestResultsRecord {
    @NonNull
    private String result;
    @NonNull
    private Integer elapsedTime;
}

