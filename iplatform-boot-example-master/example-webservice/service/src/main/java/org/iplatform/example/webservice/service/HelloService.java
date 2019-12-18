package org.iplatform.example.webservice.service;

import org.iplatform.example.webservice.bean.User;
import org.springframework.stereotype.Service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author liangruijia
 */
@Path("sayHello")
public interface HelloService {

    @GET
    @Path("/{userId}")
    @Produces(MediaType.TEXT_XML)
    User sayHello(@PathParam("userId") String useId);
}
