package com.rest.service.resteasy;

import com.rest.service.resteasy.handlers.CorsHeadersChannelHandler;
import com.rest.service.security.AnonPermission;
import com.rest.service.security.SecurePermission;
import com.rest.service.security.handler.AuthHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.jboss.resteasy.plugins.server.netty.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import static org.jboss.resteasy.plugins.server.netty.RestEasyHttpRequestDecoder.Protocol.HTTP;
import static org.jboss.resteasy.plugins.server.netty.RestEasyHttpRequestDecoder.Protocol.HTTPS;

/**
 * exposing bootstrap config
 *
 * Created by liuzh on 16-1-23.
 */
public class ConfigurableNettyJaxrsServer extends NettyJaxrsServer {

    private static Logger logger = LoggerFactory.getLogger(ConfigurableNettyJaxrsServer.class);

    private EventLoopGroup eventLoopGroup;
    private EventLoopGroup eventExecutor;
    private int ioWorkerCount = Runtime.getRuntime().availableProcessors() * 2;
    private int executorThreadCount = 16;
    private SSLContext sslContext;
    private int maxRequestSize = 1024 * 1024 * 10;
    private int backlog = 128;
    private String allowOrigin = "*";
    private SecurePermission securePermission;
    private AnonPermission anonPermission;

    @Override
    public void setSSLContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    /**
     * Specify the worker count to use. For more information about this please see the javadocs of {@link io.netty.channel.EventLoopGroup}
     *
     * @param ioWorkerCount
     */
    @Override
    public void setIoWorkerCount(int ioWorkerCount) {
        this.ioWorkerCount = ioWorkerCount;
    }

    /**
     * Set the number of threads to use for the EventExecutor. For more information please see the javadocs of {@link io.netty.util.concurrent.EventExecutor}.
     * If you want to disable the use of the {@link io.netty.util.concurrent.EventExecutor} specify a value <= 0.  This should only be done if you are 100% sure that you don't have any blocking
     * code in there.
     *
     * @param executorThreadCount
     */
    @Override
    public void setExecutorThreadCount(int executorThreadCount) {
        this.executorThreadCount = executorThreadCount;
    }

    /**
     * Set the max. request size in bytes. If this size is exceed we will send a "413 Request Entity Too Large" to the client.
     *
     * @param maxRequestSize the max request size. This is 10mb by default.
     */
    @Override
    public void setMaxRequestSize(int maxRequestSize) {
        this.maxRequestSize  = maxRequestSize;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
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

    @Override
    public void start() {
        eventLoopGroup = new NioEventLoopGroup(ioWorkerCount);
        eventExecutor = new NioEventLoopGroup(executorThreadCount);
        deployment.start();

        Assert.notNull(securePermission, "The Secure Permission implement class is not set.");

        // Configure the server.
        bootstrap.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(createChannelInitializer())
                .option(ChannelOption.SO_BACKLOG, backlog)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        bootstrap.bind(this.configuredPort).syncUninterruptibly();

        logger.info("The server start on Hostname :" + ((hostname == null) ? "127.0.0.1" : hostname) + ", Port :" + configuredPort  + ", IdleTimeout :" + getIdleTimeout() + ", The Allow Origin is :" + allowOrigin + ", Thre Root Resource Path is :" + root);
    }

    private ChannelInitializer<SocketChannel> createChannelInitializer() {
        final RequestDispatcher dispatcher = createRequestDispatcher();
        if (sslContext == null) {
            return new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    setupHandlers(ch, dispatcher, HTTP);
                }
            };
        } else {
            final SSLEngine engine = sslContext.createSSLEngine();
            engine.setUseClientMode(false);
            return new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addFirst(new SslHandler(engine));
                    setupHandlers(ch, dispatcher, HTTPS);
                }
            };
        }
    }

    private void setupHandlers(SocketChannel ch, RequestDispatcher dispatcher, RestEasyHttpRequestDecoder.Protocol protocol) {
        ChannelPipeline channelPipeline = ch.pipeline();
        channelPipeline.addLast(new HttpRequestDecoder());
        channelPipeline.addLast(new HttpObjectAggregator(maxRequestSize));
        channelPipeline.addLast(new HttpResponseEncoder());
        channelPipeline.addLast(new RestEasyHttpRequestDecoder(dispatcher.getDispatcher(), root, protocol));
        ch.pipeline().addLast(new CorsHeadersChannelHandler(allowOrigin));
        ch.pipeline().addLast(new AuthHandler(securePermission, anonPermission, allowOrigin));
        channelPipeline.addLast(new RestEasyHttpResponseEncoder());

        int idleTimeout = getIdleTimeout();
        if (idleTimeout > 0) {
            channelPipeline.addLast("idleStateHandler", new IdleStateHandler(0, 0, idleTimeout));
        }
        channelPipeline.addLast(eventExecutor, new RequestHandler(dispatcher));
    }

    @Override
    public void stop()
    {
        runtimePort = -1;
        eventLoopGroup.shutdownGracefully();
        eventExecutor.shutdownGracefully();
    }

}
