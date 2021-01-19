package com.mustr.bio;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class BioClient {

    private static String ip = "127.0.0.1";
    
    private static int port = 666;
    
    @SuppressWarnings("resource")
    public static void main(String[] args) throws UnknownHostException, IOException {
        Socket socket = new Socket(ip, port);
        
        System.out.println("请输入信息：");
        
        new ClientHandler(socket).start();
        PrintWriter pw = null;
        
        while (true) {
            pw = new PrintWriter(socket.getOutputStream());
            pw.println(new Scanner(System.in).next());
            pw.flush();
        }
        
    }
}
