package com.mustr.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 服务端处理
 * @author mustr
 * @Date 2021-1-18
 *
 */
public class NioServerHandler implements Runnable {
    
    private Selector selector;
    private ServerSocketChannel serverChannel;
    private volatile boolean started;
    
    public NioServerHandler(int port) {
        try {
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(port));
            
            //关心连接事件
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            started = true;
            
            System.out.println("服务端已启动：监听 " + port + " 端口");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    @Override
    public void run() {
        while (started) {
            try {
                //阻塞，只有至少有一个注册事件发生的时候才会继续
                selector.select();
                
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                SelectionKey key = null;
                
                while (iterator.hasNext()) {
                    key = iterator.next();
                    
                    iterator.remove();
                    try{
                        handleInput(key);
                    }catch(Exception e){
                        if(key != null){
                            key.cancel();
                            if(key.channel() != null){
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }
        
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (!key.isValid()) {
            return;
        }
        
        //新接入
        if (key.isAcceptable()) {
            //获取关心当前事件的channel
            ServerSocketChannel channel = (ServerSocketChannel)key.channel();
            
            //通过ServerSocketChannel的accept创建SocketChannel实例
            //完成该操作意味着完成TCP三次握手，TCP物理链路正式建立
            SocketChannel accept = channel.accept();
            
            System.out.println("======来客人了======");
            
            accept.configureBlocking(false);
            
            //建立完成，关心读事件
            accept.register(selector, SelectionKey.OP_READ);
        }
        
        //读事件触发
        if (key.isReadable()) {
            System.out.println("======socket channel 读取数据======");
            
            SocketChannel sc = (SocketChannel)key.channel();
            
            //创建ByteBuffer，并开辟一个1M的缓冲区
            ByteBuffer buff = ByteBuffer.allocate(1024);
            
            //读取请求码流，返回读取到的字节数
            int read = sc.read(buff);
            
            if (read > 0) {
                
                //开始读取数据
                buff.flip();
                
                //根据缓冲区可读字节数创建字节数组
                byte[] bytes = new byte[buff.remaining()];
                //将缓冲区可读字节数组复制到新建的数组中
                buff.get(bytes);
                
                String message = new String(bytes, "UTF-8");
                System.out.println("服务器收到消息：" + message);
                
                
                writeMessage(sc, message);
                
            } else {
                key.cancel();
                sc.close();
            }
        }
        
        
        
    }

    private void writeMessage(SocketChannel sc, String message) throws IOException {
        byte[] bytes = message.getBytes();
        //创建指定长度的缓冲区
        ByteBuffer buff = ByteBuffer.allocate(bytes.length);
        //放入缓冲区
        buff.put(bytes);
        
        buff.flip();
        
        sc.write(buff);
        
    }

}
