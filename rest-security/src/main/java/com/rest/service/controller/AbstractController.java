package com.rest.service.controller;

import com.rest.service.security.AuthKeyGenerator;
import com.rest.service.security.AuthenticationException;
import com.rest.service.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import static org.jboss.resteasy.util.HttpHeaderNames.AUTHORIZATION;

/**
 * Created by liuzh on 16-3-31.
 */
public abstract class AbstractController {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected AuthKeyGenerator authKeyGenerator;

    private String authorization;

    private String subject;

    @Context
    private HttpHeaders httpHeaders;

    /**
     * 对得登录用户Authorization
     * @return
     */
    protected String getAuthorization()
    {
        authorization = httpHeaders.getHeaderString(AUTHORIZATION);

        return authorization;
    }

    /**
     * 取得登录用户数据
     * @return
     * @throws AuthenticationException
     */
    protected String getSubject() throws AuthenticationException {
        subject = JwtUtils.subjectParser(authKeyGenerator.generateAuthKey(), getAuthorization());

        return subject;
    }
}
