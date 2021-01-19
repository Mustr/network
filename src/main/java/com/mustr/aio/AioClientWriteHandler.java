package com.mustr.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * aio 客户端写处理
 * @author mustr
 * @Date 2021-1-19
 *
 * Integer：本次网络写操作完成实际写入的字节数，
 * ByteBuffer：写操作的附件，存储了写操作需要写入的数据
 */
public class AioClientWriteHandler implements CompletionHandler<Integer, ByteBuffer>{

    private AsynchronousSocketChannel socketChannel;
    private CountDownLatch latch;
    
    public AioClientWriteHandler(AsynchronousSocketChannel socketChannel, CountDownLatch latch) {
        this.socketChannel = socketChannel;
        this.latch = latch;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        //有可能无法一次性将数据写完,需要检查缓冲区中是否还有数据需要继续进行网络写
        if (attachment.hasRemaining()) {
            socketChannel.write(attachment, attachment, this);
        } else {
            //写操作已经完成，为读取服务端传回的数据建立缓冲区
            ByteBuffer buff = ByteBuffer.allocate(1024);
            socketChannel.read(buff, buff, new AioClientReadHandler(socketChannel, latch));
        }
        
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        System.err.println("数据发送失败...");
        try {
            socketChannel.close();
            latch.countDown();
        } catch (IOException e) {
        }
    }

}
