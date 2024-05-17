package org.example.masterprotocolworker.model.helpers;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementBuffer {
    private static final int BUFFER_SIZE = 10;
    private List<Double> buffer = new ArrayList<>(Collections.nCopies(BUFFER_SIZE, null));
    private int index = 0;
    private int count = 0;



    public void addMeasurement(double measurement) {
        buffer.set(index, measurement);
        index = (index + 1) % BUFFER_SIZE;
        if (count < BUFFER_SIZE) {
            count++;
        }
    }

    public Double calculateMean() {
        double sum = 0;
        for (int i = 0; i < count; i++) {
            sum += buffer.get(i);
        }
        return count == 0 ? 0 : sum / count;
    }

}
