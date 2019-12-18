package org.iplatform.example.service.service;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.iplatform.microservices.core.http.RestResponse;
import org.iplatform.microservices.core.messagebus.MessageBusConsumer;
import org.iplatform.microservices.core.messagebus.MessageBusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhanglei
 */
@Configuration
@Service
@RestController
@RequestMapping("/api/v1")
public class IndexService {
    private static final Logger LOG = LoggerFactory.getLogger(IndexService.class);


    @Autowired(required = false)
    private MessageBusService messageBusService;
    /**
     * 仅演示用,发送Queue消息
     */
    @RequestMapping(value = "/sendQueueMessage", method = RequestMethod.GET)
    public ResponseEntity<RestResponse<Map>> sendQueueMessage() {
        RestResponse<String> response = new RestResponse<>();
        try {
            if(messageBusService!=null && messageBusService.isConnected()){
                messageBusService.getQueueJmsTemplate().send("Q_MS_TEST", new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage("{'msg':'hello queue message bus'}");
                    }
                });
            }
            response.setData("发送消息成功!");
            response.setSuccess(Boolean.TRUE);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error("内部错误", ex);
            response.setSuccess(Boolean.FALSE);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 仅演示用,发送Topic消息
     */
    @RequestMapping(value = "/sendTopicMessage", method = RequestMethod.GET)
    public ResponseEntity<RestResponse<Map>> sendTopicMessage() {
        RestResponse<String> response = new RestResponse<>();
        try {
            if(messageBusService!=null && messageBusService.isConnected()){
                messageBusService.getTopicJmsTemplate().send("T_MS_TEST", new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage("{'msg':'hello topic message bus'}");
                    }
                });
            }
            response.setData("发送消息成功!");
            response.setSuccess(Boolean.TRUE);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error("内部错误", ex);
            response.setSuccess(Boolean.FALSE);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
