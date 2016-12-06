package com.rest.service.demo.nodb;

import com.rest.service.security.simple.SimpleResource;
import com.shangkang.core.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzh on 16-12-6.
 */
@Service
public class ResourceService implements SimpleResource {

    private static Logger logger = LoggerFactory.getLogger(ResourceService.class);

    @Override
    public List<String> listResourcesBySubject(String subject, String token) throws ServiceException {
        List<String> list = new ArrayList<>();

        list.add("/auth/hello");
        list.add("/auth/h1");

        logger.info("it is ResourceService listResourcesBySubject");

        return list;
    }
}
