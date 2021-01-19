package com.mustr.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * 服务端连接处理
 * @author mustr
 * @Date 2021-1-18
 *
 */
public class AioAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AioServerHandler>{

    private AsynchronousServerSocketChannel serverSocketChannel;

    public AioAcceptHandler(AsynchronousServerSocketChannel serverSocketChannel) {
        this.serverSocketChannel = serverSocketChannel;
    }
    
    @Override
    public void completed(AsynchronousSocketChannel channel, AioServerHandler serverHandler) {
        System.out.println("===来客户了===");
        
        //重新注册，让别的客户端也可以连接
        serverSocketChannel.accept(serverHandler, this);
        
        ByteBuffer buff = ByteBuffer.allocate(1024);
        
        channel.read(buff, buff, new AioReadHandler(channel));
    }

    @Override
    public void failed(Throwable exc, AioServerHandler serverHandler) {
        exc.printStackTrace();
        if (serverHandler.getLatch() != null) {
            serverHandler.getLatch().countDown();
        }
    }

}
