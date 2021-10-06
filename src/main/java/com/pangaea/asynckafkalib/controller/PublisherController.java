package com.pangaea.asynckafkalib.controller;

import com.pangaea.asynckafkalib.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by  Adewale S Osobu
 */
@RestController
@RequestMapping("api/v1")
public class PublisherController {

    private PublisherService publisherService;

    @PostMapping(value = "/publish/{topic}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> performLogic(@RequestBody String request, @PathVariable String topic) {
        return publisherService.publishMessage(request, topic);
    }

    @Autowired
    public void setPublisherService(PublisherService publisherService) {
        this.publisherService = publisherService;
    }
}
