package com.rest.service.security.simple;

import com.shangkang.core.exception.ServiceException;

import java.util.List;

/**
 * Created by liuzh on 16-3-25.
 */
public interface SimpleResource {
    List<String> listResourcesBySubject(String subject, String token) throws ServiceException;
}
