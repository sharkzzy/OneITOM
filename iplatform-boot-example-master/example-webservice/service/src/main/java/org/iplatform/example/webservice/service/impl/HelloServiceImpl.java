package org.iplatform.example.webservice.service.impl;

import org.iplatform.example.webservice.bean.User;
import org.iplatform.example.webservice.service.HelloService;
import org.springframework.stereotype.Service;

/**
 * @author liangruijia
 */
@Service
public class HelloServiceImpl implements HelloService {

    @Override
    public User sayHello(String a) {
        User user = new User();
        user.setUserId("411001");
        user.setUserName("zhansan");
        user.setAge(20);
        return user;
    }
}
