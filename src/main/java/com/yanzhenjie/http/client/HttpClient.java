package com.yanzhenjie.http.client;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {

    private static final Charset UTF8 = Charset.forName("utf-8");

    private void start() throws Exception {
        System.out.println("------------------ 开始请求 ------------------");

        Socket socket = new Socket();
        socket.setSoTimeout(10 * 1000);
        socket.connect(new InetSocketAddress("www.csdn.net", 80), 10 * 1000);
//        socket.connect(new InetSocketAddress("192.168.0.111", 8888), 10 * 1000);

        System.out.println("---->>>> 发送请求 <<<<----");
        String data = "恰同学少年";
        sendRequest(socket, data.getBytes(UTF8));

        System.out.println("---->>>> 读取响应 <<<<----");
        firstRead(socket);
//        secondRead(socket);
//        thirdRead(socket);

        socket.close();
        System.out.println("------------------请求完成------------------");
    }

    /**
     * 发送请求。
     */
    private void sendRequest(Socket socket, byte[] data) throws IOException {
        OutputStream os = socket.getOutputStream();

        // 发送请求头
        PrintStream print = new PrintStream(os);
        print.println("POST /abc/dev?ab=cdf HTTP/1.1");
        print.println("Host: www.csdn.net");
        print.println("User-Agent: HttpClient/1.0");
        print.println("Content-Length: " + data.length);
        print.println("Content-Type: text/plain; charset=utf-8");
        print.println("Accept: *");
        print.println();

        // 发送请求数据
        print.write(data);
        os.flush();
    }

    /**
     * 读取响应：一次全部输出。
     */
    private void firstRead(Socket socket) throws IOException {
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
     * 读取响应：按照HTTP响应数据结构分割读取。
     */
    private void secondRead(Socket socket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        System.out.println("-->>>> 状态行 <<<<--");
        String statusLine = reader.readLine();
        System.out.println(statusLine);

        System.out.println("-->>>> 响应头 <<<<--");
        String header = reader.readLine();
        while (!"".equals(header)) {
            System.out.println(header);
            header = reader.readLine();
        }

        System.out.println("-->>>> 数据 <<<<---");
        CharArrayWriter charArray = new CharArrayWriter();
        char[] buffer = new char[2048];
        int len;
        while ((len = reader.read(buffer)) > 0) {
            charArray.write(buffer, 0, len);
            if (len < 2048) break;
        }
        System.out.println(new String(charArray.toCharArray()));
    }

    /**
     * 读取响应内容：按照HTTP响应数据结构分割读取，并适当封装。
     */
    private void thirdRead(Socket socket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        System.out.println("-->>>> 状态行 <<<<--");
        String statusLine = reader.readLine();
        System.out.println(statusLine);

        System.out.println("-->>>> 响应头 <<<<--");
        long contentLength = 0;
        Map<String, String> headers = new HashMap<>();

        String header = reader.readLine();
        while (!"".equals(header)) {
            String[] array = header.split(":");
            String key = array[0].trim();
            String value = array[1].trim();
            headers.put(key, value);

            if (key.equalsIgnoreCase("Content-Length")) {
                contentLength = Long.parseLong(value);
            }
            header = reader.readLine();
        }

        for (String key : headers.keySet()) {
            System.out.println(key + ": " + headers.get(key));
        }

        System.out.println("-->>>> 数据 <<<<--");
        CharArrayWriter charArray = new CharArrayWriter();
        char[] buffer = new char[2048];
        int totalLen = 0, len;
        while ((len = reader.read(buffer)) > 0) {
            charArray.write(buffer, 0, len);
            totalLen += len;
            if (totalLen == contentLength) break;
        }
        System.out.println(new String(charArray.toCharArray()));
    }

    public static void main(String[] args) {
        try {
            HttpClient http = new HttpClient();
            http.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
}
