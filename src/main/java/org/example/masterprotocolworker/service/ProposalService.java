package org.example.masterprotocolworker.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.masterprotocolworker.exceptions.WrongValueException;
import org.example.masterprotocolworker.model.ProducerInformation;
import org.example.masterprotocolworker.model.ProducerProposal;
import org.example.masterprotocolworker.model.Product;
import org.example.masterprotocolworker.model.Proposal;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ProposalService {

    private final ProductsService productsService;
    private final ProducerInfoService producerInfoService;


    public Proposal getProposal(List<Product> products) {
        // Step 1: Get the producers with their respective products
        Map<String, List<Product>> producerProductMap = productsService.getProducersWithProduct(products);
        Map<String, Double> producerCalculatedValues = new HashMap<>();

        // Step 2: Calculate proposal values for each producer
        producerProductMap.forEach((producer, producersProduct) -> {
            try {
                producerCalculatedValues.put(producer, calculateProposal(producer));
            } catch (WrongValueException e) {
                log.warn("{} set value to Long.MAX_VALUE", e.getMessage());
                producerCalculatedValues.put(producer, Double.MAX_VALUE);
            }
        });

        // Step 3: Find the producer with the minimal value
        String bestProducer = producerCalculatedValues.entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new RuntimeException("No producers found"));

        // Step 4: Create a map to store the final proposal for each producer
        Map<String, ProducerProposal> finalProposals = new HashMap<>();

        // Step 5: Assign products to the best producer first
        List<Product> bestProducerProducts = new ArrayList<>(producerProductMap.get(bestProducer));
        finalProposals.put(bestProducer, new ProducerProposal(producerCalculatedValues.get(bestProducer), bestProducerProducts));

        // Step 6: Remove products assigned to the best producer from other producers' lists based on product ID
        Set<String> bestProducerProductIds = bestProducerProducts.stream()
                .map(Product::getId)
                .collect(Collectors.toSet());

        for (String producer : producerProductMap.keySet()) {
            if (!producer.equals(bestProducer)) {
                List<Product> filteredProducts = producerProductMap.get(producer)
                        .stream()
                        .filter(product -> !bestProducerProductIds.contains(product.getId()))
                        .collect(Collectors.toList());
                producerProductMap.put(producer, filteredProducts);
            }
        }

        // Step 7: Ensure all products from the initial request are covered by assigning remaining products to the next best producer
        Set<String> allAssignedProductIds = finalProposals.values().stream()
                .flatMap(pp -> pp.getProducts().stream())
                .map(Product::getId)
                .collect(Collectors.toSet());

        for (Product product : products) {
            if (!allAssignedProductIds.contains(product.getId())) {
                // Find the next best producer that has this product
                for (String producer : producerCalculatedValues.keySet()) {
                    if (producerProductMap.get(producer).stream().anyMatch(p -> p.getId().equals(product.getId()))) {
                        finalProposals.computeIfAbsent(producer, k -> new ProducerProposal(producerCalculatedValues.get(producer), new ArrayList<>()))
                                .getProducts()
                                .add(product);
                        // Remove the product from other producers' lists once it is assigned
                        producerProductMap.forEach((otherProducer, otherProducts) -> {
                            if (!otherProducer.equals(producer)) {
                                otherProducts.removeIf(p -> p.getId().equals(product.getId()));
                            }
                        });
                        break;
                    }
                }
            }
        }

        // Log the final proposals for debugging
        log.info("Final proposals: {}", finalProposals);

        // Step 8: Create and return the final Proposal object
        return new Proposal(finalProposals);
    }

    private Double calculateProposal(String producer) throws WrongValueException {

        if(producer.equals("NOT-FOUND")) return Double.MAX_VALUE;

        double proposedValue = 1D;
        ProducerInformation producerInformation = this.producerInfoService.getProducerInfoMap().get(producer);

        if(producerInformation.getConnectionSpeed()!=-1L){
            proposedValue *= producerInformation.getConnectionSpeed();
        }else{
            throw new WrongValueException("Wrong value in connectionSpeed");
        }

        if(producerInformation.getConnectionTime()!=-1L){
            proposedValue *= producerInformation.getConnectionTime();
        }else{
            throw new WrongValueException("Wrong value in connectionTime");
        }

        if(producerInformation.getSystemLoadAverage1m()!=-1D){
            proposedValue *= producerInformation.getSystemLoadAverage1m();
        }else{
            throw new WrongValueException("Wrong value in systemLoadAverage1m");
        }

        return proposedValue;
    }
}
