package org.example.masterprotocolworker.api;

import com.netflix.discovery.shared.Application;
import org.example.masterprotocolworker.service.ProducerInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/producers")
public class producerInfo {

    private final ProducerInfoService producerInfoService;

    public producerInfo(ProducerInfoService producerInfoService) {
        this.producerInfoService = producerInfoService;
    }

    @GetMapping
    public List<Application> allProducersInfo(){
        return producerInfoService.printProducersInfo();
    }

}
