package com.mustr.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * 服务端读数据
 * @author mustr
 * @Date 2021-1-18
 *
 */
public class AioReadHandler implements CompletionHandler<Integer, ByteBuffer>{

    private AsynchronousSocketChannel channel;

    public AioReadHandler(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }
    
    @Override
    public void completed(Integer result, ByteBuffer attachment) {
      //如果条件成立，说明客户端主动终止了TCP套接字，这时服务端终止就可以了
        if(result == -1) {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        
        //flip操作
        attachment.flip();
        byte[] message = new byte[attachment.remaining()];
        attachment.get(message);
        try {
            String msg = new String(message, "UTF-8");
            System.out.println("接收到消息：" + msg);
            // 向客户端发送消息
            doWrite(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doWrite(String msg) {
        byte[] bytes = msg.getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        writeBuffer.put(bytes);
        writeBuffer.flip();
        
        //异步写数据
        channel.write(writeBuffer, writeBuffer, new AioWriteHandler(channel));
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        exc.printStackTrace();
        try {
            this.channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
