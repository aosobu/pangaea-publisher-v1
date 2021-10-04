package com.pangaea.fasier.controller;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
public class KafkaTestController {

    @Autowired
    ReplyingKafkaTemplate<String, String, String> kafkaTemplate;

    @Value("${kafka.topic.request-topic}")
    String requestTopic;

    @Value("${kafka.topic.requestreply-topic}")
    String requestReplyTopic;

    @PostMapping(value = "/sum", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String performLogic(@RequestBody String request) throws InterruptedException, ExecutionException {

        // create producer record
        ProducerRecord<String, String> record = new ProducerRecord<>(requestTopic, request);
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, requestReplyTopic.getBytes()));
        RequestReplyFuture<String, String, String> sendAndReceive = kafkaTemplate.sendAndReceive(record);

        // confirm if producer produced successfully
        SendResult<String, String> sendResult = sendAndReceive.getSendFuture().get();
        sendResult.getProducerRecord().headers().forEach(header -> System.out.println(header.key() + ":" + header.value().toString()));
        ConsumerRecord<String, String> consumerRecord = sendAndReceive.get();
        return consumerRecord.value();
    }
}
