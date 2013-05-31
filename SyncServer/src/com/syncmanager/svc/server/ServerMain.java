package com.syncmanager.svc.server;

import com.syncmanager.dao.FileInfoDao;
import com.syncmanager.dao.orm.FileInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerMain {
    Path uploadFolder = null;

    int port;

    public ServerMain(String s, int i) {
        Path path = Paths.get(s);
        try {
            uploadFolder = path.toRealPath(LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
            System.out.println("Ŀ¼ " + s + " ������");
            System.exit(-1);
        }
        port = i;
    }


    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java -cp <classpath> com.syncmanager.svc.server.ServerMain <upload_root_dir> <port>");
        }
        System.out.println("�ϴ����ļ�λ��Ŀ¼�� " + args[0]);
        System.out.println("�������˿ڣ� " + args[1]);

        ServerMain svc = new ServerMain(args[0], Integer.parseInt(args[1]));
        svc.start();
    }

    private void start() {
        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();
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
                    try {
                        SocketChannel sc = ssc.accept();
                        System.out.println("�ѽ��ܴ�" + sc.getRemoteAddress() + "��������");

                        //�Ϳͻ��˵������ѽ�������ʼʵ�ʴ���ͻ��˵�����
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
        System.out.println("׼������ ...");
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 1024);
        CharBuffer charbuffer;
        CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
        try {
            while (sc.read(buffer) != -1) {
                buffer.flip();
                charbuffer = decoder.decode(buffer);
                String a = charbuffer.toString();
                System.out.println("���յ� " + a);
                buffer.clear();

                //����ȷ����Ϣ
                String b = "OK";
                buffer.put(b.getBytes());
                buffer.flip();
                int n = sc.write(buffer);

                System.out.println("������ " + n + " �ֽ�");
                buffer.clear();
                SyncInfo si = new SyncInfo(a);

                handle_sync(sc, si);

            }
        } catch (IOException ex) {
            System.out.println("����������ر�����");
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
                onFileDetetion(si);
                sc.close();
                break;
            case Create:
                onFileCreation(si);
                receive_file(sc, si);
                sc.close();
                break;
            case Modify:
                onFileModification(si);
                receive_file(sc, si);
                sc.close();
                break;
        }
    }

    private long receive_file(SocketChannel sc, SyncInfo si) {
        long fileSize = 0;

        try {
            RandomAccessFile raf = new RandomAccessFile(uploadFolder.toString() + File.separator + si.getBaseFileName(), "rw");
            raf.setLength(si.getFileSize());

            FileChannel fileChannel = raf.getChannel();
            fileSize = fileChannel.transferFrom(sc, 0, si.getFileSize());
            raf.close();
            fileChannel.close();
            System.out.println("�ļ�������������С " + fileSize + " �ֽ�");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileSize;
    }

    private void onFileModification(SyncInfo si) {
        //�����ݿ��и��¼�¼
        FileInfoDao fileInfoDao = new FileInfoDao();
        FileInfo fileInfo = fileInfoDao.getfileInfoByFileName(si.getBaseFileName());
        fileInfo.setCreatedate(si.getFileTime());
        fileInfo.setFilesize(String.valueOf(si.getFileSize()));
        fileInfo.setVersion(String.valueOf(Integer.valueOf(fileInfo.getVersion())+1));
        fileInfo.setOrigPath(Paths.get(si.getFolderName()).toString());
        fileInfoDao.updatefileInfo(fileInfo);
        System.out.println("�����ļ���¼ >>> " + si.getFullFileName());
    }

    private void onFileCreation(SyncInfo si) {
        //�����ݿ�����Ӽ�¼
        FileInfoDao fileInfoDao = new FileInfoDao();
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFilename(si.getBaseFileName());
        fileInfo.setCreatedate(si.getFileTime());
        fileInfo.setUsername(si.getOwner());
        fileInfo.setFilesize(String.valueOf(si.getFileSize()));
        fileInfo.setVersion("1");
        fileInfo.setFileurl("/upload/" + si.getBaseFileName());
        fileInfo.setStorepath(uploadFolder.toString() + File.separator + si.getBaseFileName());
        fileInfo.setOrigPath(Paths.get(si.getFolderName()).toString());

        fileInfoDao.savefileInfo(fileInfo);

        System.out.println("�����ļ���¼ >>> " + si.getFullFileName());
    }

    private void onFileDetetion(SyncInfo si) {
        //�����ݿ���ɾ����¼
        FileInfoDao fileInfoDao = new FileInfoDao();
        fileInfoDao.deleteOneFile(si.getOwner(), si.getBaseFileName());

        System.out.println("ɾ���ļ� >>> " + si.getBaseFileName());
        uploadFolder.resolve(si.getBaseFileName()).toFile().delete();
    }
}