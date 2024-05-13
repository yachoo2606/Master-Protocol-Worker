package org.example.masterprotocolworker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class MasterProtocolWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MasterProtocolWorkerApplication.class, args);
    }

}
