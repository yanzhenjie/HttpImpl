package com.yanzhenjie.http.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class RequestHandler extends Thread {

    private static final Charset UTF8 = Charset.forName("utf-8");

    private Socket mSocket;

    public RequestHandler(Socket mSocket) {
        this.mSocket = mSocket;
    }

    @Override
    public void run() {
        System.out.println("---->>>> 读取请求 <<<<----");
        try {
            readRequest(mSocket);
        } catch (IOException e) {
            throw new RuntimeException("Network is wrong.");
        }

        System.out.println("---->>>> 发送响应 <<<<----");
        try {
            String data = "天上掉下个林妹妹"; // 相当于服务端API处理业务造数据
            sendResponse(mSocket, data.getBytes(UTF8));
        } catch (IOException e) {
            throw new RuntimeException("This is a bug.");
        }

        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("---->>>> 响应结束 <<<<----");
    }

    /**
     * 读取请求。
     */
    private void readRequest(Socket socket) throws IOException {
        InputStream is = socket.getInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int len;
        while ((len = is.read(buffer)) > 0) {
            bos.write(buffer, 0, len);
            if (len < 2048) break;
        }
        System.out.println(new String(bos.toByteArray()));
    }

    /**
     * 发送响应。
     */
    private void sendResponse(Socket socket, byte[] data) throws IOException {
        OutputStream os = socket.getOutputStream();

        // 发送响应头
        PrintStream print = new PrintStream(os);
        print.println("HTTP/1.1 200 Beautiful");
        print.println("Server: HttpServer/1.0");
        print.println("Content-Length: " + data.length);
        print.println("Content-Type: text/plain; charset=utf-8");
        print.println();

        // 发送响应数据
        print.write(data);
        os.flush();
    }
}
