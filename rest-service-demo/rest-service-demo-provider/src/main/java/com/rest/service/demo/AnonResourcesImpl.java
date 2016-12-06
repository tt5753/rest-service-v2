package com.rest.service.demo;

import com.alibaba.dubbo.config.annotation.Service;
import com.rest.service.security.simple.SimpleAnonResource;
import com.shangkang.core.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzh on 16-3-25.
 */
@Service
public class AnonResourcesImpl implements SimpleAnonResource {
    private static Logger logger = LoggerFactory.getLogger(AnonResourcesImpl.class);

    @Override
    public List<String> listResources() throws ServiceException {
        List<String> list = new ArrayList<>();

        list.add("/auth/login");
        logger.info("it is AnonResourcesImpl listResources");

        return list;
    }
}
