package com.mustr.nio;

/**
 * 服务端
 * @author mustr
 * @Date 2021-1-18
 *
 */
public class NioServer {

    private static NioServerHandler handler;
    private static int port = 666;
    
    
    public static void main(String[] args) {
        handler = new NioServerHandler(port);
        new Thread(handler, "nio server").start();
    }
    
}
