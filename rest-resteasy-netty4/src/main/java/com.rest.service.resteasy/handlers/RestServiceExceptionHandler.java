package com.rest.service.resteasy.handlers;

import com.rest.service.security.AuthenticationException;
import com.rest.service.security.AuthenticationForbiddenException;
import com.rest.service.utils.JsonMapper;
import com.shangkang.core.exception.ServiceException;
import org.jboss.resteasy.spi.DefaultOptionsMethodException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuzh on 16-3-22.
 */
@Component
@Provider
public class RestServiceExceptionHandler implements ExceptionMapper<Exception> {

    private static Logger logger = LoggerFactory.getLogger(RestServiceExceptionHandler.class);

    private static final String ERROR_MSG = "errorMsg";

    @Override
    public Response toResponse(Exception exception) {
        Map<String, String> resultMap = new HashMap<String, String>();
        JsonMapper jsonMapper = new JsonMapper();
        String resultJsonStr;

        if (exception instanceof AuthenticationForbiddenException)
        {
            resultMap.put(ERROR_MSG, exception.getMessage());
            resultJsonStr = jsonMapper.toJson(resultMap);
            logger.error(exception.getMessage(), exception);

            return Response.status(Response.Status.FORBIDDEN).entity(resultJsonStr).build();
        }else if (exception instanceof AuthenticationException)
        {
            resultMap.put(ERROR_MSG, exception.getMessage());
            resultJsonStr = jsonMapper.toJson(resultMap);
            logger.error(exception.getMessage(), exception);

            return Response.status(Response.Status.UNAUTHORIZED).entity(resultJsonStr).build();
        } else if (exception instanceof ServiceException) {
            resultMap.put(ERROR_MSG, exception.getMessage());
            resultJsonStr = jsonMapper.toJson(resultMap);
            logger.error(exception.getMessage(), exception);

            return Response.status(Response.Status.BAD_REQUEST).entity(resultJsonStr).build();
        } else if (exception instanceof NullPointerException) {
            resultMap.put(ERROR_MSG, "Null Pointer Exception");
            resultJsonStr = jsonMapper.toJson(resultMap);
            logger.error(exception.getMessage(), exception);

            return Response.status(Response.Status.BAD_REQUEST).entity(resultJsonStr).build();
        } else if (exception instanceof DefaultOptionsMethodException) {
            /* 前端跨域请求 */
            return Response.ok().build();
        }

        resultMap.put(ERROR_MSG, exception.getMessage());
        resultJsonStr = jsonMapper.toJson(resultMap);
        logger.error(exception.getMessage(), exception);

        return Response.status(Response.Status.BAD_REQUEST).entity(resultJsonStr).build();
    }
}
