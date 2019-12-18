package org.iplatform.example.service.service;

import org.iplatform.microservices.core.messagebus.AbstractMessageBusListener;
import org.iplatform.microservices.core.messagebus.MessageBusConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 * @author liangruijia
 */
@Service
public class BussTopicMessageService extends AbstractMessageBusListener {
    private static final Logger LOG = LoggerFactory.getLogger(IndexService.class);

    @Override
    @MessageBusConsumer(destination = "T_MS_TEST")
    public void onMessageBus(Message message){
        if(message instanceof TextMessage){
            try {
                LOG.info("Topic[Q_MS_TEST]收到消息:{}", ((TextMessage)message).getText());
            } catch (JMSException e) {
                LOG.error("消息接收错误",e);
            }
        }
    }
}
