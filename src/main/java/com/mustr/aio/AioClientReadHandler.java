package com.mustr.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * aio 客户端读处理
 * @author mustr
 * @Date 2021-1-19
 *
 * Integer：本次网络读操作完成实际写入的字节数，
 * ByteBuffer：读操作的附件，存储了写操作需要写入的数据
 */
public class AioClientReadHandler implements CompletionHandler<Integer, ByteBuffer> {

    private AsynchronousSocketChannel socketChannel;
    private CountDownLatch latch;
    
    public AioClientReadHandler(AsynchronousSocketChannel socketChannel, CountDownLatch latch) {
        this.socketChannel = socketChannel;
        this.latch = latch;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {

        attachment.flip();

        byte[] buff = new byte[attachment.remaining()];

        attachment.get(buff);

        try {
            System.out.println("接收到服务端的消息： " + new String(buff, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        System.err.println("读取数据失败...");
        try {
            socketChannel.close();
            latch.countDown();
        } catch (IOException e) {
        }
    }


}
