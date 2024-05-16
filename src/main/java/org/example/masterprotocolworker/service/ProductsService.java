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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductsService {

    private final ProducerInfoService producerInfoService;
    private final RestTemplate restTemplate;

    private Boolean hasProducerProduct(Application producer, Product product){
        ResponseEntity<Product> response = this.restTemplate.exchange(
                this.producerInfoService.getUrl(producer)+"/products/"+product.getName(),
                HttpMethod.POST,
                new HttpEntity<>(product),
                Product.class
                );

        return response.getBody()!=null;
    }


    public Map<String,List<Product>> getProducersWithProduct(List<Product> productList){
        List<Application> producers = this.producerInfoService.getProducers();

        Map<String,List<Product>> producerProductMap = new HashMap<>();

        for(Application producer : producers){
            List<Product> productsForProducer = productList.stream()
                    .filter(product -> hasProducerProduct(producer, product))
                    .toList();

            if(!productsForProducer.isEmpty()){
                producerProductMap.put(producer.getName(), productsForProducer);
            }

        }

        return producerProductMap;
    }

}
