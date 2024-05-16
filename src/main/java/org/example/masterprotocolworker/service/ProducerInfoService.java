package org.example.masterprotocolworker.service;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.masterprotocolworker.model.ProducerInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ProducerInfoService {

    @Lazy
    @Autowired
    private EurekaClient eurekaClient;

    @Autowired
    private RestTemplate restTemplate;

    @Getter
    private final Map<String, ProducerInformation> producerInfoMap = new HashMap<>();

    public List<Application> getProducers(){
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

    @Scheduled(fixedRate = 30 * 1000)
    public void testNetworkSpeed() {
        eurekaClient.getApplications().getRegisteredApplications().stream()
                .filter(service -> service.getName().startsWith("PRODUCER-"))
                .forEach(producer -> {
                    measureConnectionTime(producer);
                    measureConnectionSpeed(producer);
                });
    }

    private void measureConnectionTime(Application application) {
        eurekaClient.getApplication(application.getName()).getInstances().forEach(instanceInfo -> {
            long startTime = System.currentTimeMillis();
            try (Socket socket = new Socket(instanceInfo.getIPAddr(), instanceInfo.getPort())) {
                socket.getOutputStream().write(0);
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                getProducerInformation(instanceInfo.getAppName()).setConnectionTime(duration+1);
            } catch (IOException e) {
                getProducerInformation(instanceInfo.getAppName()).setConnectionTime(-1L);
                System.out.println("Failed to connect to " + instanceInfo.getIPAddr() + ":" + instanceInfo.getPort() + "->  " + e.getMessage());
            }
        });
    }

    private void measureConnectionSpeed(Application application) {
        eurekaClient.getApplication(application.getName()).getInstances().forEach(instanceInfo -> {
            try{
                // Create a large payload
                byte[] payload = new byte[50*1024*1024]; // 50 MB
                Arrays.fill(payload, (byte) 1); // Fill with zeros

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

                HttpEntity<?> request = new HttpEntity<>(payload, headers);

                long startTime = System.currentTimeMillis();

                ResponseEntity<Long> response = restTemplate.exchange(
                        "http://"+instanceInfo.getIPAddr()+":"+instanceInfo.getPort()+"/connectionSpeed",
                        HttpMethod.POST,
                        request,
                        Long.class
                );

                long endTime = System.currentTimeMillis();
                if(response.getStatusCode() == HttpStatus.OK){
                    long timeTaken = endTime - startTime; // Time in milliseconds
                    double timeTakenInSeconds = timeTaken / 1000.0;
                    double dataSizeInMB = payload.length / (1024.0 * 1024.0);
                    double bandwidth = dataSizeInMB / timeTakenInSeconds; // MB/s

                    getProducerInformation(instanceInfo.getAppName()).setConnectionSpeed((long) bandwidth);

                } else {
                    getProducerInformation(instanceInfo.getAppName()).setConnectionSpeed(-1L);
                }
            }catch (Exception e) {
                getProducerInformation(instanceInfo.getAppName()).setConnectionSpeed(-1L);
                System.out.println("Failed to connect to " + instanceInfo.getIPAddr() + ":" + instanceInfo.getPort() + "-> "+e.getMessage());
            }
        });
    }

    private ProducerInformation getProducerInformation(String producerName) {
        return producerInfoMap.computeIfAbsent(producerName, k -> new ProducerInformation());
    }

    public String getUrl(Application application){
        InstanceInfo instanceInfo = application.getInstances().get(0);
        return "http://"+
                instanceInfo.getIPAddr()+
                ":"+
                instanceInfo.getPort();

    }

}
