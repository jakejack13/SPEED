package edu.cornell.testoutputstream;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class TestResultsRecord {
    @NonNull
    private String result;
    @NonNull
    private Integer elapsedTime;

    @Override
    public String toString() {
        return "RESULT:" + result + ";TIME_TAKEN:" + elapsedTime;
    }

}
