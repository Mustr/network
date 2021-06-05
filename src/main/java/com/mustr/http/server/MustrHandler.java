package com.mustr.http.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

/**
 * 自定义的消息回复
 * @author Admin
 *
 */
public class MustrHandler extends ChannelInboundHandlerAdapter {

    //收到消息时，返回信息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //接收到完成的http请求
        FullHttpRequest httpRequest = (FullHttpRequest)msg;
        String result = "";
        
        try {
            String path = httpRequest.uri();
            String content = httpRequest.content().toString(CharsetUtil.UTF_8);
            HttpMethod method = httpRequest.method();
            if (!"/test".equals(path)) {
                result = "非法请求："+path;
                send(result,ctx, HttpResponseStatus.BAD_REQUEST);
                return;
            }
            
            if (HttpMethod.GET.equals(method)) {
                System.out.println("body:"+content);
                result="Get request,Response= hello netty <<>> 你好 netty.";
                send(result,ctx,HttpResponseStatus.OK);
                return;
            }
        } catch (Exception e) {
            System.out.println("处理请求失败...");
            e.printStackTrace();
        } finally {
            httpRequest.release();
        }
        
    }

    private void send(String result, ChannelHandlerContext ctx, HttpResponseStatus badRequest) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, badRequest, Unpooled.copiedBuffer(result, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    //建立连接时处理
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("建立连接, 客户端地址：" + ctx.channel().remoteAddress());
    }

    
}
