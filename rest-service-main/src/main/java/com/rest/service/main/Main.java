package com.rest.service.main;

import com.rest.service.resteasy.NettyServer;
import com.rest.service.security.AnonPermission;
import com.rest.service.security.SecurePermission;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by liuzh on 16-3-18.
 */
public class Main {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(Main.class);

    private static volatile boolean running = true;

    public static void main(String[] args){

        try {
            Properties pro = new Properties();
            try {
                Path path = Paths.get(Main.class.getResource("/rest.cfg").getPath());
                File file = path.toFile();
                if (file.exists() && file.isFile()) {
                    FileInputStream in = new FileInputStream(file);
                    pro.load(in);
                    in.close();
                    logger.info("加载系统配置文件rest.cfg...{}", pro.stringPropertyNames());
                }
            }catch (Exception e){
                logger.error("系统配置文件加载错误.",e);
                e.printStackTrace();
            }

            String xmlAC="root-context.xml",securePermission="authSecurity",anonPermission="anonAuthSecurity";
            if (pro.containsKey("xmlAC") && pro.getProperty("xmlAC")!=null && pro.getProperty("xmlAC").equals("")) {
                xmlAC = pro.getProperty("xmlAC");
            }
            if (pro.containsKey("securePermission") && pro.getProperty("securePermission")!=null && pro.getProperty("securePermission").equals("")) {
                securePermission = pro.getProperty("securePermission");
            }
            if (pro.containsKey("anonPermission") && pro.getProperty("anonPermission")!=null && pro.getProperty("anonPermission").equals("")) {
                anonPermission = pro.getProperty("anonPermission");
            }

            ApplicationContext ac = new ClassPathXmlApplicationContext(xmlAC);
            Assert.notNull(ac);

            final NettyServer netty = ac.getBean(NettyServer.class);

            netty.setSecurePermission((SecurePermission) ac.getBean(securePermission));
            netty.setAnonPermission((AnonPermission) ac.getBean(anonPermission));


            if (pro.containsKey("port") && pro.getProperty("port")!=null && pro.getProperty("port").equals("")) {
                int port = Integer.parseInt(pro.getProperty("port"));
                netty.setPort(port);
            }

            if (pro.containsKey("allowCredentials") && pro.getProperty("allowCredentials")!=null && pro.getProperty("allowCredentials").equals("")) {
                boolean allowCredentials = Boolean.getBoolean(pro.getProperty("allowCredentials"));
                netty.setAllowCredentials(allowCredentials);
            }

            if (pro.containsKey("idleTimeout") && pro.getProperty("idleTimeout")!=null && pro.getProperty("idleTimeout").equals("")) {
                int idleTimeout = Integer.parseInt(pro.getProperty("idleTimeout"));
                netty.setIdleTimeout(idleTimeout);
            }

            if (pro.containsKey("rootResourcePath") && pro.getProperty("rootResourcePath")!=null && pro.getProperty("rootResourcePath").equals("")) {
                netty.setRootResourcePath(pro.getProperty("rootResourcePath"));
            }

            if (pro.containsKey("allowOrigin") && pro.getProperty("allowOrigin")!=null && pro.getProperty("allowOrigin").equals("")) {
                netty.setAllowOrigin(pro.getProperty("allowOrigin"));
            }

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        netty.cleanUp();

                        logger.info("Rest service server {} stopped!", netty.getClass().getSimpleName());
                    } catch (Throwable t) {
                        logger.error(t.getMessage(), t);
                    }
                    synchronized (Main.class) {
                        running = false;
                        Main.class.notify();
                    }
                }
            });

            netty.start();
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
        }
    }
}
