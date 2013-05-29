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

    public int send_cmd(String cmd) {
        int bytes_sent = 0;
        CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
        //创建客户端socket
        try {
            SocketChannel socketChannel = SocketChannel.open();
            if (socketChannel.isOpen()) {
                //设置成阻塞模式
                socketChannel.configureBlocking(true);

                //发起连接
                socketChannel.connect(svr_address);

                if (socketChannel.isConnected()) {
                    ByteBuffer sendBuffer = ByteBuffer.wrap(cmd.getBytes());
                    //连接成功，开始传送数据
                    int n = socketChannel.write(sendBuffer);
                    while (n > 0) {
                        bytes_sent += n;
                        if (sendBuffer.hasRemaining()) {
                            sendBuffer.compact();
                            n = socketChannel.write(sendBuffer);
                        } else {
                            sendBuffer.clear();
                            break;
                        }
                    }

                    //接收确认消息
                    int m;
                    ByteBuffer recvBuffer = ByteBuffer.allocate(1024);
                    while ((m = socketChannel.read(recvBuffer)) != -1) {
                        recvBuffer.flip();
                        String ackMsg = decoder.decode(recvBuffer).toString();
                        System.out.println(ackMsg);
                        if (ackMsg == "OK") {
                            recvBuffer.clear();
                            //命令接收正常，关闭连接
                            System.out.println("服务端接收正常, 关闭连接.");
                            socketChannel.close();
                        }
                    }
                    if (m<0) {
                        System.out.println("服务端主动断开, 关闭连接.");
                        socketChannel.close();
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }

        return bytes_sent;
    }

    public int send_demo_cmd(String activity) {
        return send_cmd(activity);
    }

    public int send_cmd_and_file(String cmd, Path changed_file) {
        ByteBuffer cmdBuffer = ByteBuffer.wrap(cmd.getBytes());
        ByteBuffer fileBuffer = ByteBuffer.allocate(1024 * 1024);
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
                socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 1024 * 1024);
                socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
                socketChannel.setOption(StandardSocketOptions.SO_LINGER, 5);

                //发起连接
                socketChannel.connect(svr_address);

                if (socketChannel.isConnected()) {
                    //连接成功，开始传送数据
                    socketChannel.write(cmdBuffer);

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
