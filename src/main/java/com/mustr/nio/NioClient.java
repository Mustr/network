package com.mustr.nio;

import java.util.Scanner;

/**
 * nio客户端
 * @author mustr
 * @Date 2021-1-18
 *
 */
public class NioClient {

    private static int port = 666;
    private static String ip = "127.0.0.1";
    private static NioClientHandler clientHandler;
    
    
    public static void main(String[] args) {
        clientHandler = new NioClientHandler(ip, port);
        
        new Thread(clientHandler, "client").start();
        try (Scanner scanner = new Scanner(System.in)){
            while (NioClient.sendMsg(scanner.next()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
  //向服务器发送消息
    private  static boolean sendMsg(String msg) throws Exception{
        clientHandler.sendMsg(msg);
        return true;
    }
}
