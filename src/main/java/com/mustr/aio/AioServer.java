package com.mustr.aio;

/**
 * aio通信服务端
 * @author mustr
 * @Date 2021-1-18
 *
 */
public class AioServer {

    private static int port = 666;
    
    public static void main(String[] args) {
       new AioServerHandler(port).start();
    }
}
