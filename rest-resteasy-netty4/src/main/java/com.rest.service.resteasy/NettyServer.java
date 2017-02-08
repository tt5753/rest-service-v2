package com.rest.service.resteasy;

import com.rest.service.security.AnonPermission;
import com.rest.service.security.SecurePermission;
import com.rest.service.security.interceptor.SecurityInterceptor;
import org.jboss.resteasy.plugins.interceptors.CorsFilter;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.plugins.server.netty.SniConfiguration;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

import javax.annotation.PreDestroy;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.ws.rs.ext.Provider;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Collection;

import static org.jboss.resteasy.plugins.server.netty.RestEasyHttpRequestDecoder.Protocol.HTTP;
import static org.jboss.resteasy.plugins.server.netty.RestEasyHttpRequestDecoder.Protocol.HTTPS;

@Component
public class NettyServer {

    private static Logger logger = LoggerFactory.getLogger(NettyServer.class);
    @Autowired
    ApplicationContext ac;

    String rootResourcePath = "/";

    int idleTimeout = -1;

    boolean allowCredentials = true;

    int port = 8082;

    String allowedHeaders = "X-Requested-With, Authorization, Content-Type, Content-Length";

    String allowedMethods = "GET,POST,PUT,DELETE,OPTIONS";

    String allowOrigin = "*";

    int corsMaxAge = -1;

    boolean useHttps = false;

    String keystoreFile;

    String keystorePassword;

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
        if(useHttps) {
            initSniConfiguration();
        }
        netty.setDeployment(dp);
        netty.setPort(port);
        netty.setIdleTimeout(idleTimeout);
        netty.setRootResourcePath(rootResourcePath);
        netty.setSecurityDomain(null);
        netty.start();

        logger.info("The server start on url.Scheme[{}], hostname[{}], port[{}], idleTimeout[{}], allowedHeaders[{}], allowedMethods[{}], allowOrigin[{}], corsMaxAge[{}], allowCredentials[{}], rootResourcePath[{}]",
                useHttps ? HTTPS : HTTP,
                ((netty.getHostname() == null) ? "127.0.0.1" : netty.getHostname()),
                netty.getPort(),
                getIdleTimeout(),
                allowedHeaders, allowedMethods,
                allowOrigin, corsMaxAge, allowCredentials,
                rootResourcePath);
    }

    private void setInterceptor(ResteasyDeployment dp) {
        CorsFilter filter = new CorsFilter();

        filter.setAllowedMethods(allowedMethods);
        filter.setAllowedHeaders(allowedHeaders);
        filter.setAllowCredentials(allowCredentials);
        filter.setCorsMaxAge(corsMaxAge);
        filter.getAllowedOrigins().addAll(Arrays.asList(allowOrigin.split(",")));
        dp.getProviders().add(filter);

        SecurityInterceptor interceptor = new SecurityInterceptor();

        interceptor.setSecurePermission(securePermission);
        interceptor.setAnonPermission(anonPermission);

        dp.getProviders().add(interceptor);
    }

    private void initSniConfiguration() {
        try {
            Assert.notNull(keystoreFile, "The keystore File is not set.");
            Assert.notNull(keystorePassword, "The keystore Password is not set.");

            URL url = getClass().getResource(keystoreFile);
            Path path = Paths.get(url.toURI());
            File file = path.toFile();
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(file), keystorePassword.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keystorePassword.toCharArray());

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            SniConfiguration sniConfiguration = new SniConfiguration(sslContext);
            netty.setSniConfiguration(sniConfiguration);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
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

    public String getAllowedHeaders() {
        return allowedHeaders;
    }

    public void setAllowedHeaders(String allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }

    public String getAllowedMethods() {
        return allowedMethods;
    }

    public void setAllowedMethods(String allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public boolean isUseHttps() {
        return useHttps;
    }

    public void setUseHttps(boolean useHttps) {
        this.useHttps = useHttps;
    }

    public String getKeystoreFile() {
        return keystoreFile;
    }

    public void setKeystoreFile(String keystoreFile) {
        this.keystoreFile = keystoreFile;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public void setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
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

    public void setCorsMaxAge(int corsMaxAge) {
        this.corsMaxAge = corsMaxAge;
    }

    public void setSecurePermission(SecurePermission securePermission) {
        this.securePermission = securePermission;
    }

    public void setAnonPermission(AnonPermission anonPermission) {
        this.anonPermission = anonPermission;
    }

}
