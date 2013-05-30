package com.syncmanager.svc.client;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
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
                        if (ackMsg.equals("OK")) {
                            recvBuffer.clear();
                            //命令接收正常，关闭连接
                            System.out.println("服务端接收正常, 关闭连接.");
                            socketChannel.close();
                            break;
                        }
                    }
                    if (m < 0) {
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
        ByteBuffer ackBuffer = ByteBuffer.allocate(1024);

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
                    //连接成功，开始传送命令
                    System.out.println(">>>>> "+cmd);
                    socketChannel.write(cmdBuffer);

                    while (socketChannel.read(ackBuffer) != -1) {
                        ackBuffer.flip();
                        charBuffer = decoder.decode(ackBuffer);
                        ackBuffer.clear();
                        String ack = charBuffer.toString();
                        System.out.println("<<<< " + ack);
                        if (ack.equals("OK")) {
                            System.out.println("命令被服务端正常接收，开始向服务端传送文件");

                            FileInputStream fis = new FileInputStream(changed_file.toFile());
                            long offset = 0;
                            long totalBytes = changed_file.toFile().length();
                            System.out.println("总共需要传送 "+totalBytes+ " 字节");
                            FileChannel fileChannel = fis.getChannel();
                            while (offset < totalBytes) {
                                long buffSize = 1024 * 4; //每次最多传4K
                                if (totalBytes - offset < buffSize) {
                                    buffSize = totalBytes - offset;
                                }

                                long transferred = fileChannel.transferTo(offset, buffSize, socketChannel);
                                if (transferred > 0) {
                                    offset += transferred;
                                    System.out.println("已经传送 "+offset+ " 字节");
                                }
                            }

                            fileChannel.close();
                            fis.close();
                            System.out.println("+++++++++++++++++++");
                        } else {
                            System.out.println("命令接收异常");
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
