package com.pangaea.asynckafkalib.service;

import com.pangaea.asynckafkalib.controller.PublisherController;
import com.pangaea.asynckafkalib.enums.NodeKey;
import com.pangaea.asynckafkalib.util.DynamicJsonParser;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * Created by  Adewale S Osobu
 */
@Service
public class PublisherService {

    private ReplyingKafkaTemplate<String, String, String> kafkaTemplate;
    private String requestTopic;
    private String requestReplyTopic;
    private Logger logger = LoggerFactory.getLogger(PublisherController.class);

    public ResponseEntity<String> publishMessage(String request, String topic){
        ConsumerRecord<String, String> consumerRecord = null;

        try {

            ProducerRecord<String, String> record = new ProducerRecord<>(requestTopic, DynamicJsonParser
                                                                            .asJsonString(buildMessageMap(request, topic)));
            record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, requestReplyTopic.getBytes()));
            RequestReplyFuture<String, String, String> sendAndReceive = kafkaTemplate.sendAndReceive(record);

            SendResult<String, String> sendResult = sendAndReceive.getSendFuture().get();
            sendResult.getProducerRecord().headers().forEach(header -> System.out.println(header.key() + ":" + header.value().toString()));
            consumerRecord = sendAndReceive.get();

        }catch (Exception ex){
            logger.info("message publishing failed; reverted error {} " + ex.getMessage());
            return new ResponseEntity<>(consumerRecord.value(), HttpStatus.EXPECTATION_FAILED);
        }

        return new ResponseEntity<>(consumerRecord.value(), HttpStatus.OK);
    }

    private HashMap<String, Object> buildMessageMap(String request, String topic){
        HashMap<String, Object> message = new HashMap<>();
        message.put(NodeKey.PUBLISHER_TOPIC.getNodeKey(), topic);
        message.put(NodeKey.PUBLISHER_DATA.getNodeKey(), request);
        return message;
    }

    @Autowired
    public void setKafkaTemplate(ReplyingKafkaTemplate<String, String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Autowired
    public void setRequestTopic(@Value("${kafka.topic.request-topic}") String requestTopic) {
        this.requestTopic = requestTopic;
    }

    @Autowired
    public void setRequestReplyTopic(@Value("${kafka.topic.requestreply-topic}") String requestReplyTopic) {
        this.requestReplyTopic = requestReplyTopic;
    }
}
