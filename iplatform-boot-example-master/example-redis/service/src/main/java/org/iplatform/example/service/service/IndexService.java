package org.iplatform.example.service.service;

import java.util.Map;

import org.iplatform.example.service.domain.Person;
import org.iplatform.microservices.core.http.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
    protected RedisTemplate redisTemplate;

    /**
     * 仅演示用,新增用户
     */
    @RequestMapping(value = "/set", method = RequestMethod.GET)
    public ResponseEntity<RestResponse<Map>> set() {
        RestResponse<String> response = new RestResponse<>();
        try {

            Person person = new Person();
            person.setId("1");
            person.setName("yonghu1");
            person.setAge(30);
            redisTemplate.opsForValue().set(person.getId(), person);

            response.setData("新增用户成功!");
            response.setSuccess(Boolean.TRUE);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error("内部错误", ex);
            response.setSuccess(Boolean.FALSE);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 仅演示用,获取用户
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity<RestResponse<Person>> get() {
        RestResponse<Person> response = new RestResponse<>();
        try {
            Person person = (Person) redisTemplate.opsForValue().get("1");

//            redisTemplate.opsForHash()
//            redisTemplate.opsForList()
//            redisTemplate.opsForSet()
//            redisTemplate.opsForZSet()

            response.setData(person);
            response.setSuccess(Boolean.TRUE);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error("内部错误", ex);
            response.setSuccess(Boolean.FALSE);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
