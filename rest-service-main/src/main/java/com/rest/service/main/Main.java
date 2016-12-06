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
import java.io.FileNotFoundException;
import java.io.IOException;
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
                    System.out.println("加载系统重要文件rest.cfg..."+pro.stringPropertyNames());
                    logger.info("加载系统重要文件rest.cfg..."+pro.stringPropertyNames());
                }
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("系统重要文件rest.cfg遭到恶意破坏 ." +e.getMessage());
                logger.info("系统重要文件rest.cfg遭到恶意破坏",e);
            }

            String xmlAC="root-context.xml",securePermission="authSecurity",anonPermission="anonAuthSecurity";
            if (pro.containsKey("xmlAC") && pro.getProperty("xmlAC")!=null && pro.getProperty("xmlAC")!="") {
                xmlAC = pro.getProperty("xmlAC");
            }
            if (pro.containsKey("securePermission") && pro.getProperty("securePermission")!=null && pro.getProperty("securePermission")!="") {
                securePermission = pro.getProperty("securePermission");
            }
            if (pro.containsKey("anonPermission") && pro.getProperty("anonPermission")!=null && pro.getProperty("anonPermission")!="") {
                anonPermission = pro.getProperty("anonPermission");
            }

            ApplicationContext ac = new ClassPathXmlApplicationContext(xmlAC);
            Assert.notNull(ac);

            final NettyServer netty = ac.getBean(NettyServer.class);

            netty.setSecurePermission((SecurePermission) ac.getBean(securePermission));
            netty.setAnonPermission((AnonPermission) ac.getBean(anonPermission));


            if (pro.containsKey("port") && pro.getProperty("port")!=null && pro.getProperty("port")!="") {
                int port = Integer.parseInt(pro.getProperty("port"));
                netty.setPort(port);
            }

            if (pro.containsKey("idleTimeout") && pro.getProperty("idleTimeout")!=null && pro.getProperty("idleTimeout")!="") {
                int idleTimeout = Integer.parseInt(pro.getProperty("idleTimeout"));
                netty.setIdleTimeout(idleTimeout);
            }

            if (pro.containsKey("rootResourcePath") && pro.getProperty("rootResourcePath")!=null && pro.getProperty("rootResourcePath")!="") {
                netty.setRootResourcePath(pro.getProperty("rootResourcePath"));
            }

            if (pro.containsKey("allowOrigin") && pro.getProperty("allowOrigin")!=null && pro.getProperty("allowOrigin")!="") {
                netty.setAllowOrigin(pro.getProperty("allowOrigin"));
            }

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
