package org.iplatform.example.service.component;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author liangruijia
 */
@Component
public class LoggerTaskService {
    private Logger logger = LoggerFactory.getLogger(LoggerTaskService.class);

    @KafkaListener(topics = {"iplatform-myproject-service-log"},containerFactory = "kafkaListenerContainerFactory")
    private void testKafkaLogConsume(ConsumerRecord<String, String> consumer){
        logger.info("消费日志topic[{}]:{}", consumer.topic(), consumer.value());
    }
}
