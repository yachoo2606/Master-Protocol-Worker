package org.example.masterprotocolworker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetricResponse {
    private String name;
    private String description;
    private List<Measurement> measurements;
    private List<AvailableTag> availableTags;

    @Getter
    @Setter
    public static class Measurement {
        private String statistic;
        private double value;
    }

    @Getter
    @Setter
    public static class AvailableTag {
        private String tag;
        private List<String> values;
    }
}
