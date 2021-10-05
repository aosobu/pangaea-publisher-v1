package com.pangaea.asynckafkalib.service;

import com.pangaea.asynckafkalib.controller.PublisherController;
import com.pangaea.asynckafkalib.enums.NodeKey;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

/**
 * Created by  Adewale S Osobu
 */
@Service
public class PublisherService {

    private ReplyingKafkaTemplate<String, String, String> kafkaTemplate;
    private String requestTopic;
    private String requestReplyTopic;
    private Logger logger = LoggerFactory.getLogger(PublisherController.class);

    public String publishMessage(String request, String topic){
        ConsumerRecord<String, String> consumerRecord = null;
        request = addTopicAsJsonNodeToRequest(request, topic);

        try {

            ProducerRecord<String, String> record = new ProducerRecord<>(requestTopic, request);
            record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, requestReplyTopic.getBytes()));
            RequestReplyFuture<String, String, String> sendAndReceive = kafkaTemplate.sendAndReceive(record);

            SendResult<String, String> sendResult = sendAndReceive.getSendFuture().get();
            sendResult.getProducerRecord().headers().forEach(header -> System.out.println(header.key() + ":" + header.value().toString()));
            consumerRecord = sendAndReceive.get();

        }catch (Exception ex){
            logger.info("message publishing failed; reverted error {} " + ex.getMessage());
        }

        return consumerRecord.value();
    }

    private String addTopicAsJsonNodeToRequest(String request, String topic){
        return request.concat(NodeKey.PUBLISHER_TOPIC.getNodeKey().concat(topic));
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
