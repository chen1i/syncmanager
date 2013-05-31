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
            System.out.println("目录 " + s + " 不存在");
            System.exit(-1);
        }
        port = i;
    }


    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java -cp <classpath> com.syncmanager.svc.server.ServerMain <upload_root_dir> <port>");
        }
        System.out.println("上传的文件位于目录： " + args[0]);
        System.out.println("服务器端口： " + args[1]);

        ServerMain svc = new ServerMain(args[0], Integer.parseInt(args[1]));
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
                buffer.clear();
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
            System.out.println("文件接收正常，大小 " + fileSize + " 字节");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileSize;
    }

    private void onFileModification(SyncInfo si) {
        //在数据库中更新记录
        FileInfoDao fileInfoDao = new FileInfoDao();
        FileInfo fileInfo = fileInfoDao.getfileInfoByFileName(si.getBaseFileName());
        fileInfo.setCreatedate(si.getFileTime());
        fileInfo.setFilesize(String.valueOf(si.getFileSize()));
        fileInfo.setVersion(String.valueOf(Integer.valueOf(fileInfo.getVersion())+1));
        fileInfo.setOrigPath(Paths.get(si.getFolderName()).toString());
        fileInfoDao.updatefileInfo(fileInfo);
        System.out.println("更新文件记录 >>> " + si.getFullFileName());
    }

    private void onFileCreation(SyncInfo si) {
        //在数据库中添加记录
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

        System.out.println("创建文件记录 >>> " + si.getFullFileName());
    }

    private void onFileDetetion(SyncInfo si) {
        //在数据库中删除记录
        FileInfoDao fileInfoDao = new FileInfoDao();
        fileInfoDao.deleteOneFile(si.getOwner(), si.getBaseFileName());

        System.out.println("删除文件 >>> " + si.getBaseFileName());
        uploadFolder.resolve(si.getBaseFileName()).toFile().delete();
    }
}