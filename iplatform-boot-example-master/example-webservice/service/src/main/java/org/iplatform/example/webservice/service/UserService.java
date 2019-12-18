package org.iplatform.example.webservice.service;

import org.iplatform.example.webservice.bean.User;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * @author liangruijia
 */
@WebService
public interface UserService {
    /**
     * 根据用户id获取用户名称
     * @param userId 用户id
     * @return
     */
    @WebMethod
    String getName(@WebParam(name = "userId") String userId);

    /**
     * 根据用户id获取用户
     * @param userId 用户id
     * @return
     */
    @WebMethod
    User getUser(@WebParam(name = "userId") String userId);
}
