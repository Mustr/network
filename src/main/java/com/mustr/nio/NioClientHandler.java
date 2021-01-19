package com.mustr.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * nio客户端处理
 * @author mustr
 * @Date 2021-1-18
 *
 */
public class NioClientHandler implements Runnable {

    private String host;
    private int port;
    private Selector selector;
    private SocketChannel channel;
    private volatile boolean started;
    
    
    public NioClientHandler(String host, int port) {
        this.host = host;
        this.port = port;
        
        try {
            //创建选择器
            selector = Selector.open();
            //打开通道
            channel = SocketChannel.open();
            
            channel.configureBlocking(false);
            
            started = true;
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    @Override
    public void run() {
        
        connect();
        
        while (started) {
            try {
                //阻塞等待至少一个事件发生
                selector.select();
                
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                
                SelectionKey key = null;
                while (iterator.hasNext()) {
                     key = iterator.next();
                     iterator.remove();
                     
                     try {
                        handlerInput(key);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (key != null) {
                            key.cancel();
                            if(key.channel()!=null){
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

    private void handlerInput(SelectionKey key) throws IOException {
        if (!key.isValid()) {
            return;
        }
        
        SocketChannel sc = (SocketChannel)key.channel();
        
        //连接事件
        if (key.isConnectable()) {
            if(sc.finishConnect()) {
                //连接成功后关注读事件
                channel.register(selector, SelectionKey.OP_READ);
            } else {
                System.exit(1);
            }
        }
        
        //有数据可以读事件
        if (key.isReadable()) {
            ByteBuffer buff = ByteBuffer.allocate(1024);
            
            int read = sc.read(buff);
            
            if (read > 0) {
                buff.flip();
                
                byte[] bytes = new byte[buff.remaining()];
                
                buff.get(bytes);
                
                String message = new String(bytes, "utf-8");
                
                System.out.println("客户端接收到消息：" + message);
            } else {
                key.cancel();
                sc.close();
            }
            
        }
        
    }

    private void connect() {
        try {
            /*如果此通道处于非阻塞模式，
                                则调用此方法将启动非阻塞连接操作。
                                如果立即建立连接，就像本地连接可能发生的那样，则此方法返回true。
                                否则，此方法返回false，
                                稍后必须通过调用finishConnect方法完成连接操作。*/
            boolean connect = channel.connect(new InetSocketAddress(host, port));
            if (!connect) {
                //连接还未完成，所以注册连接就绪事件，向selector表示关注这个事件
                channel.register(selector, SelectionKey.OP_CONNECT);
                System.out.println("注册连接事件");
            } else {
                System.out.println("======连接上了远方=====");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public void sendMsg(String msg) {
        byte[] bytes = msg.getBytes();
        //创建缓冲区
        ByteBuffer buff = ByteBuffer.allocate(bytes.length);
        
        buff.put(bytes);
        
        buff.flip();
        
        try {
            channel.write(buff);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
