package org.iplatform.example.service.service;

import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * @author liangruijia
 */
@Component
public class ConsumerService {
    private Logger logger = LoggerFactory.getLogger(ConsumerService.class);
    @ApiOperation("Q_MS_TEST消息接收")
    @JmsListener(destination = "Q_MS_TEST", containerFactory = "queueListenerContainerFactory")
    private void receiveQueueMessage(String message){

        logger.info("QUEUE[Q_MS_TEST]:{}", message);
    }

    @ApiOperation("T_MS_TEST消息接收1")
    @JmsListener(destination = "T_MS_TEST", containerFactory = "topicListenerContainerFactory")
    private void receiveTopicMessage1(String message){

        logger.info("TOPIC[T_MS_TEST]1:{}", message);
    }

    @ApiOperation("T_MS_TEST消息接收2")
    @JmsListener(destination = "T_MS_TEST", containerFactory = "topicListenerContainerFactory")
    private void receiveTopicMessage2(String message){

        logger.info("TOPIC[T_MS_TEST]2:{}", message);
    }
}
