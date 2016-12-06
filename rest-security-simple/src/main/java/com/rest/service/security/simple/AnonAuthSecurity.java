package com.rest.service.security.simple;

//import com.alibaba.dubbo.config.annotation.Reference;
import com.rest.service.security.AnonPermission;
import com.rest.service.security.AuthenticationException;
import com.shangkang.core.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;

import java.util.List;

/**
 * Created by liuzh on 16-3-13.
 */
public class AnonAuthSecurity implements AnonPermission {

    private static Logger logger = LoggerFactory.getLogger(AnonAuthSecurity.class);

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Autowired
    private SimpleAnonResource simpleAnonResource;

    @Override
    public boolean couldAnonAccess(String uri) throws AuthenticationException {
        List<String> resourceList = null;
        try {
            resourceList = simpleAnonResource.listResources();
        } catch (ServiceException e) {
            logger.error(e.getMessage(), e);
            throw new AuthenticationException("你没有权限访问该链接");
        }
        if(resourceList == null || resourceList.isEmpty()){
            throw new AuthenticationException("你没有权限访问该链接");
        }

        for (String str : resourceList) {
            if(antPathMatcher.match(str, uri)) return true;
        }
        return false;
    }
}
