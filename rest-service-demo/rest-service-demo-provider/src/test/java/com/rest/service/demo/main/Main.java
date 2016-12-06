package com.rest.service.demo.main;

import com.rest.service.resteasy.NettyServer;
import com.rest.service.security.AnonPermission;
import com.rest.service.security.SecurePermission;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

/**
 * Created by liuzh on 16-3-11.
 */
public class Main {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(Main.class);

    private static volatile boolean running = true;

    public static void main(String[] args) {

        ApplicationContext ac = new ClassPathXmlApplicationContext("root-context.xml");
        Assert.notNull(ac);

        com.alibaba.dubbo.container.Main.main(args);
    }
}
