package com.mustr.nett;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 服务端
 * @author mustr
 *
 */
public class NettServer {

    private final int port;
    
    public NettServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        NettServer server = new NettServer(666);
        System.out.println("服务器即将启动");
        server.start();
        System.out.println("服务器关闭");
    }
    
    private void start() throws InterruptedException {
        NettServerHandler serverHander = new NettServerHandler();
        //线程组
        NioEventLoopGroup group = new NioEventLoopGroup();
        
        try {
            //启动必备
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group)
                    //指明使用NIO进行网络通信
                    .channel(NioServerSocketChannel.class)
                    //指明服务器监听端口
                    .localAddress(new InetSocketAddress(port))
                    /*接收到连接请求，新启一个socket通信，也就是channel，每个channel
                            * 有自己的事件的handler*/
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(serverHander);
                        }
                    });
            /*绑定到端口，阻塞等待直到连接完成*/
            ChannelFuture sync = bootstrap.bind().sync();
            /*阻塞，直到channel关闭*/
            sync.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
