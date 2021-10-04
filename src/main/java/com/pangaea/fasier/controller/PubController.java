package com.pangaea.fasier.controller;

import com.pangaea.fasier.model.PublishRequestBody;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

//@RestController
//@RequestMapping("api/v1")
public class PubController {

    @PostMapping(value = "publish/{topic}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void addTopic(@PathVariable String topic, @RequestBody PublishRequestBody apiRequestBody){
        //pass request body and topic to service
    }

    @PostMapping(value = "add/{topic}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void publish(@PathVariable String topic){
        //pass topic to service
    }

}
