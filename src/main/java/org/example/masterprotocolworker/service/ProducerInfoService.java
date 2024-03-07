package org.example.masterprotocolworker.service;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ProducerInfoService {

    @Lazy
    @Autowired
    private EurekaClient eurekaClient;

    @Autowired
    private RestTemplate restTemplate;

    private List<Application> getProducers(){
        List<Application> producerApplications = new ArrayList<>();
        Applications applications = eurekaClient.getApplications();
        for(Application application: applications.getRegisteredApplications()){
            for(InstanceInfo instanceInfo: application.getInstances()){
               if(instanceInfo.getAppName().startsWith("PRODUCER-")){
                   producerApplications.add(application);
               }
            }
        }
        producerApplications.forEach(System.out::println);
        return producerApplications;
    }


    public List<Application> printProducersInfo(){
        List<Application> producers = getProducers();

        producers.forEach( producer -> {
            ResponseEntity<String> response = restTemplate.getForEntity(producer.getInstances().get(0).getHomePageUrl()+"actuator/metrics/system.load.average.1m",String.class);
            log.info( producer.getName() + " Data:"+ response.getBody());
        });

        return producers;

    }


}
