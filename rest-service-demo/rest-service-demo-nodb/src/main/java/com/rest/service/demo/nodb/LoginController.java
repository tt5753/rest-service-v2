package com.rest.service.demo.nodb;

import com.rest.service.controller.AbstractController;
import com.shangkang.core.exception.ServiceException;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

/**
 * Created by liuzh on 16-12-6.
 */
@Controller
@Path("/auth")
public class LoginController extends AbstractController {

    @POST
    @Path("/login")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public User login(User user) throws ServiceException {

        user.setUsername(user.getUsername() + "testing");

        logger.info(user.getUsername());

        return user;
    }

    @POST
    @Path("/hello")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public void Hello(@Context HttpHeaders headers) throws ServiceException {
        String authorization = this.getAuthorization();//headers.getHeaderString(AUTHORIZATION);
        String subject = this.getSubject();//JwtUtils.subjectParser(authKeyGenerator.generateAuthKey(), authorization);

        logger.info("authorization = " + authorization + "; subject = " + subject);
    }

    @POST
    @Path("/h1")
    @Produces({MediaType.APPLICATION_JSON})
    public void Hello1(@QueryParam("num")long num) throws ServiceException {
        String token = this.getAuthorization();
        String subject = this.getSubject();

        System.out.println("^^^^^^^^^^^^^^^^^^^^^,token="+token+", subject="+subject);
        System.out.println("^^^^^^^^^^^^^^^^^^^^^" + num);

        throw new ServiceException("it is null");
    }
}
