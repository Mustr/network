package com.mustr.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

public class AioServerHandler extends Thread {

    private CountDownLatch latch = null;
    
    private AsynchronousServerSocketChannel channle;
    
    public AioServerHandler(int port) {
        try {
            //创建服务端的通道
            channle = AsynchronousServerSocketChannel.open();
            
            //绑定端口
            channle.bind(new InetSocketAddress(port));
            
            System.out.println("服务端开始监听 " + port + " 端口");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        latch = new CountDownLatch(1);
        
        channle.accept(this, new AioAcceptHandler(channle));
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public AsynchronousServerSocketChannel getChannle() {
        return channle;
    }


    
    
}
