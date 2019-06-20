package com.yanzhenjie.http.server;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    private void start() throws Exception {
        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress("192.168.0.111", 8888));
        System.out.println("---->>>> 服务器已经启动 <<<<----");

        while (true) {
            Socket socket = server.accept();

            InetSocketAddress address = (InetSocketAddress) socket.getRemoteSocketAddress();
            System.out.println("---->>>> 收到请求 <<<<----");
            System.out.println(address.getHostName());

            RequestHandler handler = new RequestHandler(socket);
            handler.start();
        }
    }

    public static void main(String[] args) {
        try {
            HttpServer http = new HttpServer();
            http.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
}
