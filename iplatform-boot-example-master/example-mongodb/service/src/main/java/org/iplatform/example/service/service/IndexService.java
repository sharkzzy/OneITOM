package org.iplatform.example.service.service;

import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;

import org.iplatform.example.service.domain.Person;
import org.iplatform.microservices.core.http.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private MongoTemplate mongoTemplate;

    /**
     * 仅演示用,新增
     */
    @RequestMapping(value = "/insert", method = RequestMethod.GET)
    public ResponseEntity<RestResponse<Map>> hello() {
        RestResponse<String> response = new RestResponse<>();
        try {
            Person person = new Person();
            person.setId("1");
            person.setName("yonghu1");
            person.setAge(30);
            mongoTemplate.insert(person);

            response.setData("新增成功!");
            response.setSuccess(Boolean.TRUE);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error("内部错误", ex);
            response.setSuccess(Boolean.FALSE);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * 仅演示用,查询
     */
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public ResponseEntity<RestResponse<Person>> query() {
        RestResponse<Person> response = new RestResponse<>();
        try {
            Criteria criteria = new Criteria();
            criteria = criteria.and("id").is("1");

            Query query = new Query(criteria);

            List<Person> persons = mongoTemplate.find(query, Person.class);
            response.setData(persons.size()>0?persons.get(0):null);
            response.setSuccess(Boolean.TRUE);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error("内部错误", ex);
            response.setSuccess(Boolean.FALSE);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 仅演示用,更新
     */
    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public ResponseEntity<RestResponse<Map>> update() {
        RestResponse<String> response = new RestResponse<>();
        try {
            Criteria criteria = new Criteria();
            criteria = criteria.and("id").is("1");

            Query query = new Query(criteria);

            Update update = Update.update("age", 29);

            mongoTemplate.upsert(query, update, Person.class);

            response.setData("更新成功");
            response.setSuccess(Boolean.TRUE);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error("内部错误", ex);
            response.setSuccess(Boolean.FALSE);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 仅演示用,删除
     */
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public ResponseEntity<RestResponse<Map>> delete() {
        RestResponse<String> response = new RestResponse<>();
        try {
            Criteria criteria = new Criteria();
            criteria = criteria.and("id").is("1");

            Query query = new Query(criteria);

            mongoTemplate.remove(query, Person.class);

            response.setData("删除成功");
            response.setSuccess(Boolean.TRUE);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error("内部错误", ex);
            response.setSuccess(Boolean.FALSE);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
