package com.rest.service.security.interceptor;

import com.rest.service.codec.response.StandardResult;
import com.rest.service.security.AnonPermission;
import com.rest.service.security.AuthenticationException;
import com.rest.service.security.AuthenticationForbiddenException;
import com.rest.service.security.SecurePermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.jboss.resteasy.util.HttpHeaderNames.AUTHORIZATION;

/**
 * Created by liuzh on 16-12-7.
 */
@PreMatching
public class SecurityInterceptor implements ContainerRequestFilter {

    private static Logger logger = LoggerFactory.getLogger(SecurityInterceptor.class);

    private SecurePermission securePermission;
    private AnonPermission anonPermission;

    public SecurePermission getSecurePermission() {
        return securePermission;
    }

    public void setSecurePermission(SecurePermission securePermission) {
        this.securePermission = securePermission;
    }

    public AnonPermission getAnonPermission() {
        return anonPermission;
    }

    public void setAnonPermission(AnonPermission anonPermission) {
        this.anonPermission = anonPermission;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        if(requestContext.getMethod().equalsIgnoreCase("OPTIONS")) {
            return;
        }

        StandardResult result = null;

        try {
            String uri = requestContext.getUriInfo().getPath();
            logger.debug("uri={}", uri);
            //判断当前请求的url 是否是允许匿名访问的url
            if (anonPermission.couldAnonAccess(uri)) {
                return;
            }

            //判断当前用户是否有权限访问当前的请求
            String authorization = this.getAuthorization(requestContext);
            if (securePermission.validation(uri, authorization)) {
                return;
            }
            result = new StandardResult(Response.Status.UNAUTHORIZED.getStatusCode(), "你没有权限访问该链接");
        }catch (AuthenticationForbiddenException e){
            result = new StandardResult(Response.Status.FORBIDDEN.getStatusCode(), e.getMessage());
        }catch (AuthenticationException e){
            result = new StandardResult(Response.Status.UNAUTHORIZED.getStatusCode(), e.getMessage());
        }

        Response.ResponseBuilder builder = Response.ok(result, MediaType.APPLICATION_JSON).status(result.getErrorCode());

        requestContext.abortWith(builder.build());
    }

    protected String getAuthorization(ContainerRequestContext request) {
        return request.getHeaderString(AUTHORIZATION);
    }
}
