package com.mustr.http.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;

public class MustrClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpResponse httpResponse = (FullHttpResponse) msg;
        try {
            System.out.println(httpResponse.headers());

            String content = httpResponse.content().toString(CharsetUtil.UTF_8);
            System.out.println(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpResponse.release();
        }

    }

    
}
