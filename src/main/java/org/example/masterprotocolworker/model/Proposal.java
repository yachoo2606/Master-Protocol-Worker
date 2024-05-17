package org.example.masterprotocolworker.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Proposal {
    Map<String, ProducerProposal> proposalProducts;
}
