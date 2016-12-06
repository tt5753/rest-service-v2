package com.rest.service.demo.facade;

import com.rest.service.demo.bo.User;
import com.shangkang.core.exception.ServiceException;

/**
 * Created by liuzh on 16-3-31.
 */
public interface UserDemoFacade {
    public User login(User user) throws ServiceException;
}
