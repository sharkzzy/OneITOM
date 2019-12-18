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
public class BussQueueMessageService extends AbstractMessageBusListener {
    private static final Logger LOG = LoggerFactory.getLogger(IndexService.class);

    @Override
    @MessageBusConsumer(destination = "Q_MS_EVENT")
    public void onMessageBus(Message message){
        if(message instanceof TextMessage){
            try {
                LOG.info("Queue[Q_MS_EVENT]收到消息:{}",((TextMessage)message).getText());
            } catch (JMSException e) {
                LOG.error("消息接收错误",e);
            }
        }
    }
}
