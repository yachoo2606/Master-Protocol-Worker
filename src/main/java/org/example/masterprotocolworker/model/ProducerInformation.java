package org.example.masterprotocolworker.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProducerInformation {
    private Long connectionSpeed;
    private Long connectionTime;
    private Double systemLoadAverage1m;
}
