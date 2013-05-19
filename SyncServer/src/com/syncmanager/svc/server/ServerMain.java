package com.syncmanager.svc.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class ServerMain {
    final int DEFAULT_PORT = 5555;
    final String IP = "127.0.0.1";

    int port;

    public ServerMain(String s) {
        port = Integer.parseInt(s);
    }


    public static void main(String[] args) {
        System.out.println("################################");
        System.out.printf("Server start on port %s ...\n", args.length > 0 ? args[0] : "5555");
        System.out.println("################################");

        ServerMain svc = new ServerMain(args.length > 0 ? args[0] : "5555");
        svc.start();
    }

    private void start() {
        //Java 7 的新语法
        try (ServerSocketChannel ssc = ServerSocketChannel.open()) {
            if (ssc.isOpen()) {
                //服务器端socket工作于阻塞模式
                ssc.configureBlocking(true);

                //设定一些socket参数
                ssc.setOption(StandardSocketOptions.SO_RCVBUF, 1024 * 4);
                ssc.setOption(StandardSocketOptions.SO_REUSEADDR, true);

                //绑定到指定的监听端口
                ssc.bind(new InetSocketAddress(port));

                //整个流程是串行的，每次只能处理一个连接
                while (true) {
                    //开始接受连接请求，程序会阻塞在这一步直到有连接请求进来
                    try (SocketChannel sc = ssc.accept()) {
                        System.out.println("已接受从" + sc.getRemoteAddress() + "来的连接");

                        //和客户端的连接已建立，开始实际处理客户端的数据
                        handle_connection(sc);
                    } catch (IOException ex) {
                        //do nothing
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handle_connection(SocketChannel sc) throws IOException {
        System.out.println("We are going to receive something ...");
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        CharBuffer charbuffer;
        CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
        while (sc.read(buffer) != -1) {
            buffer.flip();
            charbuffer = decoder.decode(buffer);
            String a = charbuffer.toString();
            System.out.println("接收到 " + a);
            buffer.clear();
            String b = a.toUpperCase();
            buffer.put(b.getBytes());
            buffer.flip();
            int n = sc.write(buffer);
            System.out.println("发送了 "+n+" 字节");
            if (buffer.hasRemaining()) {
                System.out.println(">>>>>>>>>>>>>>>");
                buffer.compact();
            } else {
                System.out.println("<<<<<<<<<<<<<<<");
                buffer.clear();
            }
        }
    }
}