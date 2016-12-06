package com.rest.service.demo.facade.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.rest.service.demo.bo.User;
import com.rest.service.demo.facade.UserDemoFacade;
import com.rest.service.security.AuthKeyGenerator;
import com.rest.service.utils.JwtUtils;
import com.shangkang.core.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by liuzh on 16-3-31.
 */
@Service
public class UserDemoFacadeImpl implements UserDemoFacade {

    private static Logger logger = LoggerFactory.getLogger(UserDemoFacadeImpl.class);

    @Autowired
    protected AuthKeyGenerator authKeyGenerator;

    @Override
    public User login(User user) throws ServiceException {
        if(user.getUsername() == null || user.getPassword() == null
                || !user.getPassword().equals(user.getUsername()))
        {
            throw new ServiceException("用户名密码错误!");
        }
        logger.info("*******************:" + user);

        //TODO 登陆成功后：记录当前用户的登陆状态
        //登陆session失效时间是否与token失效时间一致？

        //生成token字符串
        String token = JwtUtils.tokenBuilder(authKeyGenerator.generateAuthKey(), user.getUsername(), 500000);

        user.setToken(token);
        return user;
    }
}
