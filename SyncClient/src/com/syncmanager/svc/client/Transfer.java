package com.syncmanager.svc.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Path;
import java.util.Random;

public class Transfer {
    private InetSocketAddress svr_address;

    public Transfer(InetSocketAddress server) {
        svr_address = server;
    }

    public int send_file_to_server(String activity) {
        //创建客户端socket
        try {
            SocketChannel socketChannel = SocketChannel.open();
            if (socketChannel.isOpen()) {
                //设置成阻塞模式
                socketChannel.configureBlocking(true);

                //发起连接
                socketChannel.connect(svr_address);

                if (socketChannel.isConnected()) {
                    ByteBuffer sendBuffer = ByteBuffer.wrap(activity.getBytes());
                    //连接成功，开始传送数据
                    while(socketChannel.write(sendBuffer)>0) {
                        if (sendBuffer.hasRemaining())
                            sendBuffer.compact();
                        else {
                            sendBuffer.clear();
                            break;
                        }
                    }

                    //接收确认消息
                    ByteBuffer recvBuffer = ByteBuffer.allocate(1024);
                    while(socketChannel.read(recvBuffer) != -1) {
                        recvBuffer.flip();
                        System.out.println(Charset.defaultCharset().newDecoder()
                                .decode(recvBuffer).toString());
                        recvBuffer.clear();
                    }
                    System.out.println("Server shutdown connection, close client side.");
                    socketChannel.close();
                }
            }
        }catch (IOException ex) {
            System.err.println(ex);
        }

        return 0;
    }

    public int real_send_file_to_server(Path changed_file) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ByteBuffer helloBuffer = ByteBuffer.wrap("Hello !".getBytes());
        CharBuffer charBuffer;
        CharsetDecoder decoder = Charset.defaultCharset().newDecoder();

        //创建客户端socket
        try {
            SocketChannel socketChannel = SocketChannel.open();
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
                    System.out.println("Server shutdown connection, close client side.");
                    socketChannel.close();
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
