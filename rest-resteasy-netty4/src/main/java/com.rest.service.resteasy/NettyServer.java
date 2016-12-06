package com.rest.service.resteasy;

import com.rest.service.resteasy.handlers.CorsHeadersChannelHandler;
import com.rest.service.security.AnonPermission;
import com.rest.service.security.SecurePermission;
import com.rest.service.security.handler.AuthHandler;
import io.netty.channel.ChannelHandler;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class NettyServer {

    private static Logger logger = LoggerFactory.getLogger(NettyServer.class);
    @Autowired
    ApplicationContext ac;

    String rootResourcePath = "/";

    int idleTimeout = -1;

    int port = 8082;

    String allowOrigin = "*";

    ConfigurableNettyJaxrsServer netty;

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
        // extract only controller annotated beans
        dp.getResources().addAll(controllers);

        netty = new ConfigurableNettyJaxrsServer();
        netty.setDeployment(dp);
        netty.setPort(port);
        netty.setIdleTimeout(idleTimeout);
        netty.setRootResourcePath(rootResourcePath);
        netty.setSecurityDomain(null);
        netty.setAllowOrigin(allowOrigin);
        netty.setSecurePermission(securePermission);
        netty.setAnonPermission(anonPermission);
        netty.start();

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
