package org.example.masterprotocolworker.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProducerProposal {
    private long proposedValue;
    private List<Product> products;
}
