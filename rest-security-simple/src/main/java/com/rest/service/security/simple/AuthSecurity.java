package com.rest.service.security.simple;


import com.rest.service.security.AuthKeyGenerator;
//import com.alibaba.dubbo.config.annotation.Reference;
import com.rest.service.security.AuthenticationException;
import com.rest.service.security.SecurePermission;
import com.rest.service.utils.JwtUtils;
import com.shangkang.core.exception.ServiceException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;

import java.util.List;

/**
 * Created by liuzh on 16-3-11.
 */
public class AuthSecurity implements SecurePermission {

    private static Logger logger = LoggerFactory.getLogger(AuthSecurity.class);

    @Autowired(required = false)
    private AuthKeyGenerator authKeyGenerator;

    @Autowired
    private SimpleResource simpleResource;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    public boolean validation(String uri, String authorization) throws AuthenticationException {
        String subject = null;

        if(authKeyGenerator == null) authKeyGenerator = new DefaultAuthKeyGenerator();

        subject = JwtUtils.subjectParser(authKeyGenerator.generateAuthKey(), authorization);

        //验证当前用户是否有访问当前资源的权限(Restful风格的url)
        List<String> resourceList = null;
        try {
            resourceList = simpleResource.listResourcesBySubject(subject, authorization);
        } catch (ServiceException e) {
            logger.error(e.getMessage(), e);
            throw new AuthenticationException("你没有权限访问该链接");
        }
        if(resourceList == null || resourceList.isEmpty()){
            throw new AuthenticationException("你没有权限访问该链接");
        }
        for(String str : resourceList){
            if(antPathMatcher.match(str, uri)) return true;
        }
        return false;
    }
}
