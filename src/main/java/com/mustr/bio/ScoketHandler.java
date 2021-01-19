package com.mustr.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 客户端
 * @author mustr
 * @Date 2021-1-18
 *
 */
public class ScoketHandler implements Runnable {

    private Socket socket;
    
    public ScoketHandler(Socket socket) {
        super();
        this.socket = socket;
    }

    public void run() {

        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String message = null;

            while ((message = in.readLine()) != null) {
                System.out.println("接收到消息： " + message);

                out.println("回复你：" + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
