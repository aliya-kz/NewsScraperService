package org.zhumagulova.newsscraperservice.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.zhumagulova.newsscraperservice.entity.News;

@Component
@Slf4j
public class KafkaSender {

    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    KafkaSender(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(News news, String topicName) {
        try {
            log.debug("Producing message : " + news);
            kafkaTemplate.send(topicName, news).get();
        } catch (Exception e) {
            log.debug("Exception occured while sending message: " + e);
            throw new RuntimeException(e);
        }
    }
}
