package org.example.masterprotocolworker.api;

import com.netflix.discovery.shared.Application;
import org.example.masterprotocolworker.service.ProducerInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/producers")
public class ProducerInfoController {

    private final ProducerInfoService producerInfoService;

    public ProducerInfoController(ProducerInfoService producerInfoService) {
        this.producerInfoService = producerInfoService;
    }

    @GetMapping
    public List<Application> allProducersInfo(){
        return producerInfoService.printProducersInfo();
    }

    @GetMapping("/speedtime")
    public ResponseEntity<?> allSpeedTimeInfo(){
        return ResponseEntity.ok().body(this.producerInfoService.getProducerInfoMap());
    }


}
