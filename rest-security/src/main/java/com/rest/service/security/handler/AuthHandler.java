package com.rest.service.security.handler;

import com.rest.service.codec.response.StandardResponse;
import com.rest.service.codec.response.StandardResult;
import com.rest.service.security.AnonPermission;
import com.rest.service.security.AuthenticationException;
import com.rest.service.security.AuthenticationForbiddenException;
import com.rest.service.security.SecurePermission;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.jboss.resteasy.plugins.server.netty.NettyHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;

import static org.jboss.resteasy.util.HttpHeaderNames.AUTHORIZATION;

/**
 * Created by liuzh on 16-3-10.
 */
@Sharable
public class AuthHandler extends SimpleChannelInboundHandler<NettyHttpRequest> {
    private static Logger logger = LoggerFactory.getLogger(AuthHandler.class);
    private SecurePermission securePermission;
    private AnonPermission anonPermission;
    private String allowOrigin = "*";

    public AuthHandler(SecurePermission securePermission) {
        this.securePermission = securePermission;
    }

    public AuthHandler(SecurePermission securePermission, AnonPermission anonPermission, String allowOrigin) {
        this.securePermission = securePermission;
        this.anonPermission = anonPermission;
        this.allowOrigin = allowOrigin;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, NettyHttpRequest request) throws Exception {
        if (request.getHttpMethod().equalsIgnoreCase("OPTIONS")) {
            channelHandlerContext.fireChannelRead(request);
            return;
        }

        StandardResult result = null;
        try {
            String uri = request.getUri().getMatchingPath();
            logger.debug("uri=" + uri);
            //判断当前请求的url 是否是允许匿名访问的url
            if (anonPermission.couldAnonAccess(uri)) {
                channelHandlerContext.fireChannelRead(request);
                return;
            }

            //判断当前用户是否有权限访问当前的请求
            String authorization = this.getAuthorization(request);
            if (securePermission.validation(uri, authorization)) {
                channelHandlerContext.fireChannelRead(request);
                return;
            }
            result = new StandardResult(Response.Status.UNAUTHORIZED.getStatusCode(), "你没有权限访问该链接");
        }catch (AuthenticationForbiddenException e){
            result = new StandardResult(Response.Status.FORBIDDEN.getStatusCode(), e.getMessage());
        }catch (AuthenticationException e){
            result = new StandardResult(Response.Status.UNAUTHORIZED.getStatusCode(), e.getMessage());
        }
        StandardResponse.write(channelHandlerContext, result, allowOrigin);
        return;
    }

    protected String getAuthorization(NettyHttpRequest request) {
        return request.getHttpHeaders().getHeaderString(AUTHORIZATION);
    }

}
