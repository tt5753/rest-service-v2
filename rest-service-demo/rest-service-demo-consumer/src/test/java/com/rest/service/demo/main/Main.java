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

    public static void main(String[] args){

        com.rest.service.main.Main.main(args);
        /*try {
            ApplicationContext ac = new ClassPathXmlApplicationContext("root-context.xml");
            Assert.notNull(ac);

            NettyServer netty = ac.getBean(NettyServer.class);

            netty.setSecurePermission((SecurePermission) ac.getBean("authSecurity"));
            netty.setAnonPermission((AnonPermission) ac.getBean("anonAuthSecurity"));
            netty.setPort(9000);
            netty.start();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        netty.cleanUp();

                        logger.info("Rest service server " + netty.getClass().getSimpleName() + " stopped!");
                    } catch (Throwable t) {
                        logger.error(t.getMessage(), t);
                    }
                    synchronized (Main.class) {
                        running = false;
                        Main.class.notify();
                    }
                }
            });

        } catch (RuntimeException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            System.exit(1);
        }

        synchronized (Main.class) {
            while (running) {
                try {
                    Main.class.wait();
                } catch (Throwable e) {
                }
            }
        }*/
    }
}
