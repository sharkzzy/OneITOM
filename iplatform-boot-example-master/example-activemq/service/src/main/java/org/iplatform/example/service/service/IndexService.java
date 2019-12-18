package org.iplatform.example.service.service;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.jms.Queue;
import javax.jms.Topic;

import org.iplatform.microservices.core.http.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
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

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Queue Q_MS_TEST;

    @Autowired
    private Topic T_MS_TEST;

    /**
     * 仅演示用,发送queue消息
     */
    @RequestMapping(value = "/sendQueue", method = RequestMethod.GET)
    public ResponseEntity<RestResponse<Map>> sendQueue() {
        RestResponse<String> response = new RestResponse<>();
        try {
            //发送queue消息
            jmsTemplate.convertAndSend(Q_MS_TEST, "hello queue");

            response.setData("发送成功!");
            response.setSuccess(Boolean.TRUE);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error("内部错误", ex);
            response.setSuccess(Boolean.FALSE);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 仅演示用,发送topic消息
     */
    @RequestMapping(value = "/sendTopic", method = RequestMethod.GET)
    public ResponseEntity<RestResponse<Map>> sendTopic() {
        RestResponse<String> response = new RestResponse<>();
        try {
            //发送topic消息
            jmsTemplate.convertAndSend(T_MS_TEST, "hello topic");

            response.setData("发送成功");
            response.setSuccess(Boolean.TRUE);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error("内部错误", ex);
            response.setSuccess(Boolean.FALSE);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

