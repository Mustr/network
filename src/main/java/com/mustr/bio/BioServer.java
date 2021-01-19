package com.mustr.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

/**
 * 服务端
 * @author mustr
 * @Date 2021-1-18
 *
 */
public class BioServer {
    private static BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<Runnable>();
    
    private static ThreadFactory factory = new BasicThreadFactory.Builder().namingPattern("thread %d ...").build();
    private static ExecutorService pool = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, workQueue, factory);
    
    private static int PORT = 666;
    
    public static void main(String[] args) {
        try (ServerSocket socket = new ServerSocket(PORT)) {

            System.out.println("服务已启动，监听 " + PORT + " 端口...");

            while (true) {
                Socket accept = socket.accept();

                System.out.println("====来客人了====");

                pool.execute(new ScoketHandler(accept));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
