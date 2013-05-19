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
        //Java 7 �����﷨
        try (ServerSocketChannel ssc = ServerSocketChannel.open()) {
            if (ssc.isOpen()) {
                //��������socket����������ģʽ
                ssc.configureBlocking(true);

                //�趨һЩsocket����
                ssc.setOption(StandardSocketOptions.SO_RCVBUF, 1024 * 4);
                ssc.setOption(StandardSocketOptions.SO_REUSEADDR, true);

                //�󶨵�ָ���ļ����˿�
                ssc.bind(new InetSocketAddress(port));

                //���������Ǵ��еģ�ÿ��ֻ�ܴ���һ������
                while (true) {
                    //��ʼ�����������󣬳������������һ��ֱ���������������
                    try (SocketChannel sc = ssc.accept()) {
                        System.out.println("�ѽ��ܴ�" + sc.getRemoteAddress() + "��������");

                        //�Ϳͻ��˵������ѽ�������ʼʵ�ʴ����ͻ��˵�����
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
            System.out.println("���յ� " + a);
            buffer.clear();
            String b = a.toUpperCase();
            buffer.put(b.getBytes());
            buffer.flip();
            int n = sc.write(buffer);
            System.out.println("������ "+n+" �ֽ�");
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