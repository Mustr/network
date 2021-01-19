package com.mustr.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * aio 客户端连接处理
 * @author mustr
 * @Date 2021-1-19
 *
 */
public class AioClientHandler implements Runnable, CompletionHandler<Void, AioClientHandler>{

    private String ip;
    private int port;
    private AsynchronousSocketChannel socketChannel;
    private CountDownLatch latch;
    
    public AioClientHandler(String ip, int port) {
        this.ip = ip;
        this.port = port;
        
        try {
            socketChannel = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        latch = new CountDownLatch(1);
        
        socketChannel.connect(new InetSocketAddress(ip, port), null, this);
        
        try {
            latch.await();
            socketChannel.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessag(String msg) {
        byte[] bytes = msg.getBytes();
        
        ByteBuffer buff = ByteBuffer.allocate(bytes.length);
        
        buff.put(bytes);
        
        buff.flip();
        
        socketChannel.write(buff, buff, new AioClientWriteHandler(socketChannel, latch));
    }

    @Override
    public void completed(Void result, AioClientHandler attachment) {
        System.out.println("连接上服务器");
    }

    @Override
    public void failed(Throwable exc, AioClientHandler attachment) {
        System.out.println("连接失败...");
        exc.printStackTrace();
        latch.countDown();
        
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
