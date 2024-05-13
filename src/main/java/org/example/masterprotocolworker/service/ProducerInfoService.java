package org.example.masterprotocolworker.service;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
public class ProducerInfoService {

    @Lazy
    @Autowired
    private EurekaClient eurekaClient;

    private final RestTemplate restTemplate = new RestTemplate();

    private List<Application> getProducers(){
        List<Application> producerApplications = eurekaClient.getApplications().getRegisteredApplications().stream()
                .filter(service -> service.getName().startsWith("PRODUCER-")).toList();
        producerApplications.forEach(System.out::println);
        return producerApplications;
    }


    public List<Application> printProducersInfo(){
        List<Application> producers = getProducers();

        producers.forEach( producer -> {
            ResponseEntity<String> response = restTemplate.getForEntity(producer.getInstances().get(0).getHomePageUrl()+"actuator/metrics",String.class);
            log.info( producer.getName() + " Data:"+ response.getBody());
        });

        return producers;

    }


}
