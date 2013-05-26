package com.syncmanager.svc.client;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;


public class FolderMonitor implements Runnable {
    private WatchService watchService;
    private final Map<WatchKey, Path> directories = new HashMap();
    private Path start_folder;
    private Transfer comm;

    public FolderMonitor(String path) {
        start_folder = Paths.get(path);
    }

    public void setTransfer(Transfer transfer) {
        comm = transfer;
    }


    @Override
    public void run() {
        System.out.println("开始定时扫描目录... ");
        try {
            watchRNDir(start_folder);
        } catch (IOException ex){
            System.err.println(ex);
        } catch (InterruptedException ex) {
            System.err.println(ex);
        }
    }

    private void registerPath(Path path) throws IOException {
        //注册要扫瞄的目录，感兴趣的事件是创建，改动和删除文件或目录
        WatchKey key = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
        //记住要扫瞄的目录
        directories.put(key, path);
    }

    private void registerTree(Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                System.out.println("Registering:" + dir);
                registerPath(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void watchRNDir(Path start) throws IOException, InterruptedException {
        watchService = FileSystems.getDefault().newWatchService();
        registerTree(start);
        //服务运行于死循环模式
        while (true) {
            //取出一个有改动的事件key
            final WatchKey key = watchService.take();
            //遍历所有的事件
            for (WatchEvent<?> watchEvent : key.pollEvents()) {
                //得到事件类型（创建，改动，删除）
                final WatchEvent.Kind<?> kind = watchEvent.kind();
                //事件发生的文件
                final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
                final Path filename = watchEventPath.context();
                //忽略过时的事件
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }
                //对于创建文件的事件，如果该文件是个目录，需要把这个新加的目录也加到扫瞄列表里
                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    final Path directory_path = directories.get(key);
                    final Path child = directory_path.resolve(filename);
                    if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                        registerTree(child);
                    }
                }

                //用于演示，发送有改动的文件名给服务器
                String activity = kind.toString() + " -> " + filename;
                System.out.println(activity);
                comm.send_file_to_server(activity);

            }
            //重置key
            boolean valid = key.reset();
            //如果该key已失效，则从扫瞄列表里删除
            if (!valid) {
                directories.remove(key);
                //如果不再扫瞄任何目录，则退出整个死循环，终止服务
                if (directories.isEmpty()) {
                    break;
                }
            }
        }
        watchService.close();
    }
}
