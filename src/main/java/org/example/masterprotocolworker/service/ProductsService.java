package org.example.masterprotocolworker.service;


import com.netflix.discovery.shared.Application;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.masterprotocolworker.model.Product;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductsService {

    private final ProducerInfoService producerInfoService;
    private final RestTemplate restTemplate;

    private Product hasProducerProduct(Application producer, Product product){
        ResponseEntity<Product> response = this.restTemplate.exchange(
                this.producerInfoService.getUrl(producer)+"/products/check",
                HttpMethod.POST,
                new HttpEntity<>(product),
                Product.class
                );

        return response.getBody()!=null ? response.getBody() : null;
    }


    public Map<String, List<Product>> getProducersWithProduct(List<Product> productList) {
        List<Application> producers;
        try {
            producers = this.producerInfoService.getProducers();
        } catch (Exception e) {
            // Log the error and return an empty map or handle the error as needed
            System.err.println("Failed to retrieve producers from Eureka: " + e.getMessage());
            return Collections.singletonMap("ERROR", Collections.singletonList(new Product()));
        }

        Map<String, List<Product>> producerProductMap = new HashMap<>();
        List<Product> notFoundProducts = new ArrayList<>();

        for (Product product : productList) {
            boolean found = false;
            for (Application producer : producers) {
                try {
                    Product response = hasProducerProduct(producer, product);
                    if (response != null) {
                        producerProductMap
                                .computeIfAbsent(producer.getName(), k -> new ArrayList<>())
                                .add(response);
                        found = true;
                    }
                } catch (Exception e) {
                    // Log the error and continue with the next producer
                    log.error("Error checking product in producer {}: {}", producer.getName(), e.getMessage());
                }
            }
            if (!found) {
                notFoundProducts.add(product);
            }
        }

        if (!notFoundProducts.isEmpty()) {
            producerProductMap.put("NOT-FOUND", notFoundProducts);
        }

        return producerProductMap;
    }

}
