package org.example.masterprotocolworker.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.masterprotocolworker.model.helpers.MeasurementBuffer;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProducerInformation {
    private MeasurementBuffer measurementBufferConnectionSpeed = new MeasurementBuffer();
    private MeasurementBuffer measurementBufferConnectionTime = new MeasurementBuffer();
    private MeasurementBuffer measurementBufferSystemLoadAverage1m = new MeasurementBuffer();

    public double getConnectionSpeed() {
        return measurementBufferConnectionSpeed.calculateMean();
    }

    public double getConnectionTime() {
        return measurementBufferConnectionTime.calculateMean();
    }

    public double getSystemLoadAverage1m() {
        return measurementBufferSystemLoadAverage1m.calculateMean();
    }
}
