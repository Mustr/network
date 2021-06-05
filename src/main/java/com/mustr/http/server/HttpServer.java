package com.mustr.http.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * http服务端
 * @author mustr
 *
 */
public class HttpServer {

    public static final int PORT = 6666;
    
    private static EventLoopGroup group = new NioEventLoopGroup();
    private static ServerBootstrap bootstrap = new ServerBootstrap();
    
    public static void main(String[] args) {
        
        try {
        bootstrap.group(group);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ServerHandlerInit());
        
            ChannelFuture sync = bootstrap.bind(PORT).sync();
            System.out.println("服务端启动成功，端口号为：" + PORT);
            //监听服务关闭
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
        
    }
    
}
