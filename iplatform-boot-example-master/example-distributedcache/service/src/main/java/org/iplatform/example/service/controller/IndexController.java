package org.iplatform.example.service.controller;

import org.iplatform.example.service.domain.Person;
import org.iplatform.example.service.service.IndexService;
import org.iplatform.microservices.core.http.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author liangruijia
 */
@RestController
@RequestMapping("/api/v1")
public class IndexController {
    private Logger LOG = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private IndexService indexService;

    /**
     * 仅演示用,添加缓存
     */
    @RequestMapping(value = "/addCache", method = RequestMethod.GET)
    public ResponseEntity<RestResponse<Map>> addCache() {
        RestResponse<String> response = new RestResponse<>();
        try {
            Person person = new Person();
            person.setId("2");
            person.setName("yonghu1");
            person.setAge(30);

            indexService.addPerson(person);
            response.setData("新增成功！");
            response.setSuccess(Boolean.TRUE);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error("内部错误", ex);
            response.setSuccess(Boolean.FALSE);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 仅演示用,修改缓存
     */
    @RequestMapping(value = "/getCache", method = RequestMethod.GET)
    public ResponseEntity<RestResponse<Person>> getCache() {
        RestResponse<Person> response = new RestResponse<>();
        try {
            Person person = indexService.getPerson("1");

            response.setData(person);
            response.setSuccess(Boolean.TRUE);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error("内部错误", ex);
            response.setSuccess(Boolean.FALSE);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 仅演示用,修改缓存
     */
    @RequestMapping(value = "/modifyCache", method = RequestMethod.GET)
    public ResponseEntity<RestResponse<Map>> modifyCache() {
        RestResponse<String> response = new RestResponse<>();
        try {
            Person person = new Person();
            person.setId("1");
            person.setName("yonghu1");
            person.setAge(29);

            indexService.modifyPerson(person);

            response.setData("修改成功！");
            response.setSuccess(Boolean.TRUE);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error("内部错误", ex);
            response.setSuccess(Boolean.FALSE);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 仅演示用,删除缓存
     */
    @RequestMapping(value = "/removeCache", method = RequestMethod.GET)
    public ResponseEntity<RestResponse<Map>> removeCache() {
        RestResponse<String> response = new RestResponse<>();
        try {
            indexService.removePerson("1");

            response.setData("删除成功!");
            response.setSuccess(Boolean.TRUE);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error("内部错误", ex);
            response.setSuccess(Boolean.FALSE);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
