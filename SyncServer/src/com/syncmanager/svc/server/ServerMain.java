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

import static com.syncmanager.svc.server.SyncInfo.Activity.*;

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
        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();
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
                    try {
                        SocketChannel sc = ssc.accept();
                        System.out.println("已接受从" + sc.getRemoteAddress() + "来的连接");

                        //和客户端的连接已建立，开始实际处理客户端的数据
                        handle_connection(sc);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handle_connection(SocketChannel sc) {
        System.out.println("准备接收 ...");
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 1024);
        CharBuffer charbuffer;
        CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
        try {
            while (sc.read(buffer) != -1) {
                buffer.flip();
                charbuffer = decoder.decode(buffer);
                String a = charbuffer.toString();
                System.out.println("接收到 " + a);
                buffer.clear();

                //发送确认消息
                String b = "OK";
                buffer.put(b.getBytes());
                buffer.flip();
                int n = sc.write(buffer);

                System.out.println("发送了 " + n + " 字节");

                SyncInfo si = new SyncInfo(a);

                handle_sync(sc, si);

            }
        } catch (IOException ex) {
            System.out.println("传输结束，关闭连接");
            try {
                sc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void handle_sync(SocketChannel sc, SyncInfo si) throws IOException {
        switch (si.getAct()) {
            case Delete:
                onFileDetetion(si.getFileName());
                sc.close();
                break;
            case Create:
                onFileCreation(si);
                receive_file(sc);
                sc.close();
                break;
            case Modify:
                onFileModification(si);
                receive_file(sc);
                break;
        }
    }
}