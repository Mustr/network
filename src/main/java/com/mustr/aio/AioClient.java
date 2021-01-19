package com.mustr.aio;

import java.util.Scanner;

/**
 * aio 客户端
 * @author mustr
 * @Date 2021-1-19
 *
 */
public class AioClient {

    private static String ip = "127.0.0.1";
    private static int port = 666;
    
    
    private static AioClientHandler handler;
    
    public static void main(String[] args) {
        handler = new AioClientHandler(ip, port);
        
        new Thread(handler, "aio client...").start();
        
        
        try (Scanner sc = new Scanner(System.in)) {
            while(sendMsg(sc.next()));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
  //向服务器发送消息
    public static boolean sendMsg(String msg) throws Exception{
        if(msg.equals("q")) return false;
        handler.sendMessag(msg);
        return true;
    }
    
    
}
