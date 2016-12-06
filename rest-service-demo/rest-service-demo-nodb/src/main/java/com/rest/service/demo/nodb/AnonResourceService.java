package com.rest.service.demo.nodb;

import java.util.ArrayList;
import java.util.List;

import com.rest.service.security.simple.SimpleAnonResource;
import com.shangkang.core.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by liuzh on 16-12-6.
 */
@Service
public class AnonResourceService implements SimpleAnonResource {
    private static Logger logger = LoggerFactory.getLogger(AnonResourceService.class);

    @Override
    public List<String> listResources() throws ServiceException {
        List<String> list = new ArrayList<>();

        list.add("/auth/login");
        logger.info("it is AnonResourceService listResources");

        return list;
    }
}
