package org.iplatform.example.service.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;

import org.iplatform.example.service.component.MyAppConfig;
import org.iplatform.microservices.core.IPlatformApplication;
import org.iplatform.microservices.core.http.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertySource;
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

    @Value("${myapp.username}")
    private String username;

    @Value("${myapp.counter}")
    private Integer counter;

    @Value("${myapp.enabled}")
    private Boolean enabled;

    @Value("${myapp.notexist:default}")
    private String notexist;

    @Value("${myapp.array:first,second,third}")
    private String[] array;

    @Value("#{'${myapp.array:first,second,third}'.split(',')}")
    private List<String> arrayList;

    @Autowired
    private MyAppConfig myAppConfig;
    /**
     * 仅演示用,静态获取变量
     */
    @RequestMapping(value = "/first", method = RequestMethod.GET)
    public ResponseEntity<RestResponse<Map>> first() {
        RestResponse<String> response = new RestResponse<>();
        try {
            LOG.info("字符串变量:{},数字变量:{},BOOL变量:{},数组变量:{},列表变量:{},默认值变量:{}", username, counter, enabled, array,arrayList, notexist);
            response.setData("Hi");
            response.setSuccess(Boolean.TRUE);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error("内部错误", ex);
            response.setSuccess(Boolean.FALSE);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 仅演示用,静态获取变量
     */
    @RequestMapping(value = "/second", method = RequestMethod.GET)
    public ResponseEntity<RestResponse<Map>> second() {
        RestResponse<String> response = new RestResponse<>();
        try {
            LOG.info("自定义配置:{}", myAppConfig);
            response.setData("Hi");
            response.setSuccess(Boolean.TRUE);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error("内部错误", ex);
            response.setSuccess(Boolean.FALSE);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 仅演示用,静态获取变量
     */
    @RequestMapping(value = "/third", method = RequestMethod.GET)
    public ResponseEntity<RestResponse<Map>> third() {
        RestResponse<String> response = new RestResponse<>();
        try {
            String password_ = IPlatformApplication.getEnvironment().getProperty("myapp.password");
            int counter_ = IPlatformApplication.getEnvironment().getProperty("myapp.counter", Integer.class);
            boolean enabled_ = IPlatformApplication.getEnvironment().getProperty("myapp.enabled", Boolean.class);
            String[] array_ = IPlatformApplication.getEnvironment().getProperty("myapp.array", String[].class);
            String default_ = IPlatformApplication.getEnvironment().getProperty("myapp.notexists", "default");
            LOG.info("字符串变量:{},数字变量:{},BOOL变量:{},数组变量:{},默认值变量:{}", password_, counter_, enabled_, array_, default_);
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
