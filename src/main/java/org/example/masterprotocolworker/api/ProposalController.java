package org.example.masterprotocolworker.api;


import lombok.RequiredArgsConstructor;
import org.example.masterprotocolworker.model.Product;
import org.example.masterprotocolworker.service.ProductsService;
import org.example.masterprotocolworker.service.ProposalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/proposal")
@RequiredArgsConstructor
public class ProposalController {

    private final ProductsService productsService;
    private final ProposalService proposalService;

    @PostMapping
    public ResponseEntity<?> getProposal(@RequestBody List<Product> products){
        return ResponseEntity.ok().body(proposalService.getProposal(products));
    }

    @PostMapping("/productProducerCheck")
    public ResponseEntity<?> productProducerCheck(@RequestBody List<Product> products){
            return ResponseEntity.ok().body(productsService.getProducersWithProduct(products));
    }

}
