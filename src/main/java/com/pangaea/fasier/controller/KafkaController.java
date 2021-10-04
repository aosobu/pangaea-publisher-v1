package com.pangaea.fasier.controller;

import com.pangaea.fasier.service.KafaMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaController {

    @Autowired
    private KafaMessageSender kafaMessageSender;

    @PostMapping("/pushMessage")
    public String postMessage(@RequestBody String message){
        kafaMessageSender.sendMessage(message);
        return "Message Published";
    }
}
