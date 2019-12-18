package org.iplatform.example.service.service;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.iplatform.microservices.core.http.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@Service
@RestController
@RequestMapping("/api/v1")
public class IndexService {
    private static final Logger LOG = LoggerFactory.getLogger(IndexService.class);

    @PostConstruct
    public void init() {
        LOG.info("类实例化");
    }

    /**
     * 仅演示用
     */
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public ResponseEntity<RestResponse<Map>> hello() {
        RestResponse<String> response = new RestResponse<>();
        try {
            response.setData("Hi");
            response.setSuccess(Boolean.TRUE);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error("内部错误", ex);
            response.setSuccess(Boolean.FALSE);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
