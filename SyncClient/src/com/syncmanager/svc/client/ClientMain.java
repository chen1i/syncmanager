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

    int port;
    String server_ip;

    public ClientMain(String[] args) {
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

    }


    public static void main(String[] args) {
        ClientMain instance = new ClientMain(args);
        instance.start_monitoring();
    }

    private void start_monitoring() {
        //当前固定监视demo目录
        FolderMonitor fm = new FolderMonitor("../demo");
        //设置一个负责传送的通信对象
        fm.setTransfer(new Transfer(new InetSocketAddress(server_ip, port)));
        Thread monitor = new Thread(fm);
        monitor.start();
    }
}

