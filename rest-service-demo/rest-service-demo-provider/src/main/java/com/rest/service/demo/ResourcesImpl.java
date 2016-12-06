package com.rest.service.demo;

import com.alibaba.dubbo.config.annotation.Service;
import com.rest.service.security.simple.SimpleResource;
import com.shangkang.core.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzh on 16-3-29.
 */
@Service
public class ResourcesImpl implements SimpleResource {

    private static Logger logger = LoggerFactory.getLogger(ResourcesImpl.class);

    @Override
    public List<String> listResourcesBySubject(String subject, String token) throws ServiceException {
        List<String> list = new ArrayList<>();

        list.add("/auth/hello");
        list.add("/auth/h1");

        logger.info("it is ResourcesImpl listResourcesBySubject");

        return list;
    }
}
