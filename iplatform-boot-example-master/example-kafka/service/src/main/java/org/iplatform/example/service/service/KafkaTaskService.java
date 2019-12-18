package org.iplatform.example.service.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author liangruijia
 */
@Component
public class KafkaTaskService {

    private Logger logger = LoggerFactory.getLogger(KafkaTaskService.class);

    @KafkaListener(topics = {"topic_test"},containerFactory = "kafkaListenerContainerFactory")
    private void testKafkaConsume(ConsumerRecord<String, String> consumer){
        logger.info("消费topic[{}]:{}", consumer.topic(), consumer.value());
    }
}
