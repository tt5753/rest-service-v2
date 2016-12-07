package com.rest.service.resteasy;

import com.rest.service.security.AnonPermission;
import com.rest.service.security.SecurePermission;
import com.rest.service.security.interceptor.SecurityInterceptor;
import org.jboss.resteasy.plugins.interceptors.CorsFilter;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

import javax.annotation.PreDestroy;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.Collection;

@Component
public class NettyServer {

    private static Logger logger = LoggerFactory.getLogger(NettyServer.class);
    @Autowired
    ApplicationContext ac;

    String rootResourcePath = "/";

    int idleTimeout = -1;

    boolean allowCredentials = true;

    int port = 8082;

    String allowOrigin = "*";

    NettyJaxrsServer netty;

    SecurePermission securePermission;

    AnonPermission anonPermission;

    public void start() {

        ResteasyDeployment dp = new ResteasyDeployment();

        Collection<Object> providers = ac.getBeansWithAnnotation(Provider.class).values();
        Collection<Object> controllers = ac.getBeansWithAnnotation(Controller.class).values();

        Assert.notEmpty(controllers);

        // extract providers
        if (providers != null) {
            dp.getProviders().addAll(providers);
        }
        setInterceptor(dp);

        Assert.notNull(securePermission, "The secure permission implement class is not set.");
        Assert.notNull(anonPermission, "The anonymous permission implement class is not set.");

        // extract only controller annotated beans
        dp.getResources().addAll(controllers);

        netty = new NettyJaxrsServer();
        netty.setDeployment(dp);
        netty.setPort(port);
        netty.setIdleTimeout(idleTimeout);
        netty.setRootResourcePath(rootResourcePath);
        netty.setSecurityDomain(null);
        netty.start();

        logger.info("The server start on hostname:{}, port:{}, idleTimeout:{}, allowOrigin:{}, allowCredentials:{}, rootResourcePath:{}",
                ((netty.getHostname() == null) ? "127.0.0.1" : netty.getHostname()),
                netty.getPort(),
                getIdleTimeout(),
                allowOrigin, allowCredentials,
                rootResourcePath);
    }

    private void setInterceptor(ResteasyDeployment dp) {
        CorsFilter filter = new CorsFilter();

        filter.setAllowedMethods("GET,POST,PUT,DELETE,OPTIONS");
        filter.setAllowedHeaders("X-Requested-With, Authorization, Content-Type, Content-Length");
        filter.setAllowCredentials(allowCredentials);
        filter.getAllowedOrigins().addAll(Arrays.asList(allowOrigin.split(",")));
        dp.getProviders().add(filter);

        SecurityInterceptor interceptor = new SecurityInterceptor();

        interceptor.setSecurePermission(securePermission);
        interceptor.setAnonPermission(anonPermission);

        dp.getProviders().add(interceptor);
    }

    @PreDestroy
    public void cleanUp() {
        netty.stop();
    }

    public String getRootResourcePath() {
        return rootResourcePath;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public boolean isAllowCredentials() {
        return allowCredentials;
    }

    public void setAllowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public void setRootResourcePath(String rootResourcePath) {
        this.rootResourcePath = rootResourcePath;
    }

    public void setAllowOrigin(String allowOrigin) {
        this.allowOrigin = allowOrigin;
    }

    public void setSecurePermission(SecurePermission securePermission) {
        this.securePermission = securePermission;
    }

    public void setAnonPermission(AnonPermission anonPermission) {
        this.anonPermission = anonPermission;
    }

}
