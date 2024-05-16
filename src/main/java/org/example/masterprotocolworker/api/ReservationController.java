package org.example.masterprotocolworker.api;


import lombok.RequiredArgsConstructor;
import org.example.masterprotocolworker.model.Product;
import org.example.masterprotocolworker.service.ProductsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/proposal")
@RequiredArgsConstructor
public class ReservationController {

    private final ProductsService productsService;

    @PostMapping
    public ResponseEntity<?> getProposal(@RequestBody List<Product> products){
            return ResponseEntity.ok().body(productsService.getProducersWithProduct(products));
    }

}
