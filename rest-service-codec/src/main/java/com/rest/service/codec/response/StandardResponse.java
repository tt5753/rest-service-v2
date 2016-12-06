package com.rest.service.codec.response;

import com.rest.service.utils.JsonMapper;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import javax.ws.rs.core.MediaType;
import java.nio.charset.Charset;

import static org.jboss.resteasy.util.HttpHeaderNames.CONTENT_LENGTH;
import static org.jboss.resteasy.util.HttpHeaderNames.CONTENT_TYPE;

/**
 * Created by lins on 16-2-24.
 */
public class StandardResponse {
    private final static JsonMapper JSON = JsonMapper.nonDefaultMapper();

    public static void write(ChannelHandlerContext ctx, StandardResult result, String allowOrigin){
        String json = JSON.toJson(result);

        HttpResponse httpResponse = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.valueOf(result.getErrorCode()),
                Unpooled.copiedBuffer(json,
                        Charset.defaultCharset()));
        httpResponse.headers().set(CONTENT_LENGTH, json.getBytes().length);

        httpResponse.headers().set(CONTENT_TYPE, MediaType.APPLICATION_JSON);
        httpResponse.headers().set("Access-Control-Allow-Origin", allowOrigin);
        httpResponse.headers().set("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        httpResponse.headers().set("Access-Control-Allow-Headers", "X-Requested-With, Authorization, Content-Type, Content-Length");
        ctx.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
    }
}
