package org.example.masterprotocolworker.service;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
public class NetworkSpeedTesterService {


    private final EurekaClient discoveryClient;

    @Getter
    private final Map<String, ProducerInformation> producerInfoMap = new HashMap<>();

    public NetworkSpeedTesterService(EurekaClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Scheduled(fixedRate = 30 * 1000)
    public void testNetworkSpeed() {
        discoveryClient.getApplications().getRegisteredApplications().stream()
                .filter(service -> service.getName().startsWith("PRODUCER-"))
                .forEach(producer -> {
                    measureConnectionTime(producer);
                    measureConnectionSpeed(producer);
                });
    }

    private void measureConnectionTime(Application application) {
        discoveryClient.getApplication(application.getName()).getInstances().forEach(instanceInfo -> {
            long startTime = System.currentTimeMillis();
            try (Socket socket = new Socket(instanceInfo.getIPAddr(), instanceInfo.getPort())) {
                socket.getOutputStream().write(0);
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                getProducerInformation(instanceInfo.getAppName()).setConntime(duration);
            } catch (IOException e) {
                getProducerInformation(instanceInfo.getAppName()).setConntime(-1L);
                System.out.println("Failed to connect to " + instanceInfo.getIPAddr() + ":" + instanceInfo.getPort());
            }
        });
    }

    private void measureConnectionSpeed(Application application) {
        byte[] data = new byte[1024 * 1024]; // 1 MB of data
        discoveryClient.getApplication(application.getName()).getInstances().forEach(instanceInfo -> {
            long startTime = System.currentTimeMillis();
            try (Socket socket = new Socket(instanceInfo.getIPAddr(), instanceInfo.getPort());
                 OutputStream out = socket.getOutputStream()) {
                out.write(data); // Send the data
                out.flush();
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;

                if (duration > 0) {
                    double speed = (data.length * 8) / (duration / 1000.0); // Speed in bits per second
                    getProducerInformation(instanceInfo.getAppName()).setConnspeed((long) (speed / 1_000_000));
                } else {
                    getProducerInformation(instanceInfo.getAppName()).setConnspeed(-1L);
                }
            } catch (IOException e) {
                getProducerInformation(instanceInfo.getAppName()).setConnspeed(-1L);
                System.out.println("Failed to connect to " + instanceInfo.getIPAddr() + ":" + instanceInfo.getPort());
            }
        });
    }

    private ProducerInformation getProducerInformation(String producerName) {
        return producerInfoMap.computeIfAbsent(producerName, k -> new ProducerInformation());
    }

    @Getter
    @Setter
    private static class ProducerInformation {
        private Long connspeed;
        private Long conntime;
    }
}
