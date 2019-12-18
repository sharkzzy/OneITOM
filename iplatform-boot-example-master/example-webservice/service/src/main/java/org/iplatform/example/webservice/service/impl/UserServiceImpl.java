package org.iplatform.example.webservice.service.impl;

import org.iplatform.example.webservice.bean.User;
import org.iplatform.example.webservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.jws.WebService;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liangruijia
 */
@WebService(targetNamespace = "http://service.webservice.example.iplatform.org/", endpointInterface = "org.iplatform.example.webservice.service.UserService")
@Service
public class UserServiceImpl implements UserService {
    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private Map<String, User> userMap = new HashMap<>();

    @PostConstruct
    public void init(){
        logger.info("插入人员数据");
        User user = new User();
        user.setUserId("411001");
        user.setUserName("zhansan");
        user.setAge(20);
        userMap.put(user.getUserId(), user);

        user = new User();
        user.setUserId("411002");
        user.setUserName("lisi");
        user.setAge(30);
        userMap.put(user.getUserId(), user);

        user = new User();
        user.setUserId("411003");
        user.setUserName("wangwu");
        user.setAge(40);
        userMap.put(user.getUserId(), user);
    }
    @Override
    public String getName(String userId) {
        String userName = null;
        if (userMap.containsKey(userId)){
            userName = userMap.get(userId).getUserName();
        }
        return userName;
    }

    @Override
    public User getUser(String userId) {
        User user = null;
        if (userMap.containsKey(userId)){
            user = userMap.get(userId);
        }
        return user;
    }
}
