package com.mustr.nett;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 客户端
 * @author mustr
 *
 */
public class NettClient {

    private final int port;
    private final String host;

    public NettClient(int port, String host) {
        this.port = port;
        this.host = host;
    }
    
    public static void main(String[] args) throws InterruptedException {
        NettClient client = new NettClient(666, "127.0.0.1");
        
        client.start();
    }
    
    private void start() throws InterruptedException {
        //线程组
        NioEventLoopGroup group = new NioEventLoopGroup();
        
        try {
            Bootstrap boot = new Bootstrap();
            boot.group(group).channel(NioSocketChannel.class).remoteAddress(new InetSocketAddress(host, port))
                    .handler(new NettClientHandler());
            /*连接到远程节点，阻塞等待直到连接完成*/
            ChannelFuture sync = boot.connect().sync();
            /*阻塞，直到channel关闭*/
            sync.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
        
    }
}
