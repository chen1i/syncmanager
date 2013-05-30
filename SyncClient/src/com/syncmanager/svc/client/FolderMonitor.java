package com.syncmanager.svc.client;

import com.syncmanager.dao.FileInfoDao;
import com.syncmanager.dao.UserInfoDao;
import com.syncmanager.dao.orm.FileInfo;
import com.syncmanager.dao.orm.UserInfo;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FolderMonitor implements Runnable {
    private WatchService watchService;
    private final Map<WatchKey, Path> directories = new HashMap();
    private Path start_folder;
    List<Path> monitor_list = new ArrayList<Path>();
    private Transfer comm;

    public FolderMonitor(String path) {
        start_folder = Paths.get(path);
    }

    public FolderMonitor(String user, String password) {
        UserInfo ui = (new UserInfoDao()).getAlluserInfoByUserNameAndPassword(user, password, "2");
        if (ui == null)
            throw new RuntimeException("User name or password wrong");
        List<FileInfo> myfiles = (new FileInfoDao()).getAllfileInfoByUserName(ui.getUsername());
        if (myfiles == null || myfiles.isEmpty())
            throw new RuntimeException("No files to sync");
        else {
            for (FileInfo fi : myfiles) {
                Path p = Paths.get(fi.getOrigPath() + "/");
                try {
                    Path realp = p.toRealPath(LinkOption.NOFOLLOW_LINKS);
                    System.out.println("will monitor >>> " + realp.toString());
                    monitor_list.add(realp);
                } catch (IOException e) {
                    System.out.println("Cannot access: " + p.toString());
                }
            }
        }
    }

    public void setTransfer(Transfer transfer) {
        comm = transfer;
    }


    @Override
    public void run() {
        System.out.println("开始定时扫描目录... ");
        try {
            watchService = FileSystems.getDefault().newWatchService();
            for (Path e : monitor_list) {
                if (e.toFile().isDirectory())
                    registerTree(e);
                else
                    registerPath(e);
            }

            watchRNDir();
        } catch (IOException ex) {
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

    private void watchRNDir() throws IOException, InterruptedException {
        //服务运行于死循环模式
        while (true) {
            //取出一个有改动的事件key
            final WatchKey key = watchService.take();
            Path dir = (Path) key.watchable();
            //遍历所有的事件
            for (WatchEvent<?> watchEvent : key.pollEvents()) {
                //得到事件类型（创建，改动，删除）
                final WatchEvent.Kind<?> kind = watchEvent.kind();
                //事件发生的文件
                final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
                final Path filename = dir.resolve(watchEventPath.context());
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    //忽略过时的事件
                    // do nothing
                } else if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    //对于创建文件的事件，如果该文件是个目录，需要把这个新加的目录也加到扫瞄列表里
                    final Path directory_path = directories.get(key);
                    final Path child = directory_path.resolve(filename);
                    if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                        registerTree(child);
                    } else {
                        onFileCreation(child);
                    }
                } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    //改变过的文件需要重新上传
                    onFileModification(filename);
                } else {
                    //删除的文件也要在服务端删除
                    onFileDeletion(filename);
                }
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
        }// infinity loop
        watchService.close();
    }

    private void onFileDeletion(Path filename) {
        String cmd= new SyncActivity(filename, SyncActivity.Activity.Delete).toCmdString();
        comm.send_cmd(cmd);
    }

    private void onFileModification(Path filename) {
        String cmd = new SyncActivity(filename, SyncActivity.Activity.Modify).toCmdString();
        comm.send_cmd_and_file(cmd, filename);
    }

    private void onFileCreation(Path child) {
        String cmd = new SyncActivity(child, SyncActivity.Activity.Create).toCmdString();
        comm.send_cmd_and_file(cmd, child);
    }
}
