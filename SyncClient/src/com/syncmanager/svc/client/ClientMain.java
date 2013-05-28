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
    private String user;
    private String password;
    private int port;
    private String server_ip;


    public ClientMain(String[] args) {
        if (args.length < 4) {
            System.out.println("Usage: java -cp <classpath> com.syncmanager.svc.client.ClientMain <user> <password> <server> <port>");
            System.exit(-1);
        }

        user = args[0];
        password = args[1];
        server_ip = args[2];
        port = Integer.parseInt(args[3]);

    }

    public static void main(String[] args) {
        ClientMain instance = new ClientMain(args);
        instance.start_monitoring();
    }

    private void start_monitoring() {
        //FolderMonitor会从DB中获得要监视的目录和文件
        FolderMonitor fm = new FolderMonitor(user, password);
        //设置一个负责传送的通信对象
        fm.setTransfer(new Transfer(new InetSocketAddress(server_ip, port)));
        Thread monitor = new Thread(fm);
        monitor.start();
    }
}

