package com.syncmanager.svc.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Random;

public class ClientMain {

    final int DEFAULT_PORT = 5555;
    final String DEFAULT_SERVER_IP = "127.0.0.1";

    InetSocketAddress svr_address;

    public ClientMain(String[] args) {
        int port;
        String server_ip;

        if (args.length >= 2) {
            port = Integer.parseInt(args[1]);
            server_ip = args[0];
        } else if (args.length == 1) {
            port = DEFAULT_PORT;
            server_ip = args[0];
        } else {
            port = DEFAULT_PORT;
            server_ip = DEFAULT_SERVER_IP;
        }

        svr_address = new InetSocketAddress(server_ip, port);
    }


    public static void main(String[] args) {
        ClientMain instance = new ClientMain(args);
        instance.send_file_to_server();
    }

    private int send_file_to_server() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ByteBuffer helloBuffer = ByteBuffer.wrap("Hello !".getBytes());
        ByteBuffer randomBuffer;
        CharBuffer charBuffer;
        CharsetDecoder decoder = Charset.defaultCharset().newDecoder();

        //创建客户端socket
        try (SocketChannel socketChannel = SocketChannel.open()) {
            if (socketChannel.isOpen()) {
                //设置成阻塞模式
                socketChannel.configureBlocking(true);
                //其他可选项
                socketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 128 * 1024);
                socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 128 * 1024);
                socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
                socketChannel.setOption(StandardSocketOptions.SO_LINGER, 5);

                //发起连接
                socketChannel.connect(svr_address);

                if (socketChannel.isConnected()) {
                    //连接成功，开始传送数据
                    socketChannel.write(helloBuffer);
                    while (socketChannel.read(buffer) != -1) {
                        buffer.flip();
                        charBuffer = decoder.decode(buffer);
                        System.out.println(charBuffer.toString());
                        if (buffer.hasRemaining()) {
                            System.out.println(">>>>>>>>>>>>>>");
                            buffer.compact();
                        } else {
                            System.out.println("<<<<<<<<<<<<<<<");
                            buffer.clear();
                        }
                        int r = new Random().nextInt(100);
                        if (r == 50) {
                            System.out.println("50 was generated! Close the socket channel!");
                            break;
                        } else {
                            String rand_string = "Random number:".concat(String.valueOf(r));
                            buffer.clear();
                            buffer.put(rand_string.getBytes());
//                            buffer.asCharBuffer().put(rand_string);
                            buffer.flip();
                            socketChannel.write(buffer);
                            if (buffer.hasRemaining()) {
                                System.out.println("===============");
                                buffer.compact();
                            } else {
                                System.out.println("---------------");
                                buffer.clear();
                            }


                        }
                    }
                } else {
                    System.out.println("The connection cannot be established!");
                }
            } else {
                System.out.println("The socket channel cannot be opened!");
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return 0;
    }
}

