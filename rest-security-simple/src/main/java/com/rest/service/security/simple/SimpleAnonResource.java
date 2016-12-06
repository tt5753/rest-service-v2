package com.rest.service.security.simple;

import com.shangkang.core.exception.ServiceException;

import java.util.List;

/**
 * Created by liuzh on 16-3-25.
 */
public interface SimpleAnonResource {
    List<String> listResources() throws ServiceException;
}
