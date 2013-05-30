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
        System.out.println("��ʼ��ʱɨ��Ŀ¼... ");
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
        //ע��Ҫɨ���Ŀ¼������Ȥ���¼��Ǵ������Ķ���ɾ���ļ���Ŀ¼
        WatchKey key = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
        //��סҪɨ���Ŀ¼
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
        //������������ѭ��ģʽ
        while (true) {
            //ȡ��һ���иĶ����¼�key
            final WatchKey key = watchService.take();
            Path dir = (Path) key.watchable();
            //�������е��¼�
            for (WatchEvent<?> watchEvent : key.pollEvents()) {
                //�õ��¼����ͣ��������Ķ���ɾ����
                final WatchEvent.Kind<?> kind = watchEvent.kind();
                //�¼��������ļ�
                final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
                final Path filename = dir.resolve(watchEventPath.context());
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    //���Թ�ʱ���¼�
                    // do nothing
                } else if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    //���ڴ����ļ����¼���������ļ��Ǹ�Ŀ¼����Ҫ������¼ӵ�Ŀ¼Ҳ�ӵ�ɨ���б���
                    final Path directory_path = directories.get(key);
                    final Path child = directory_path.resolve(filename);
                    if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                        registerTree(child);
                    } else {
                        onFileCreation(child);
                    }
                } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    //�ı�����ļ���Ҫ�����ϴ�
                    onFileModification(filename);
                } else {
                    //ɾ�����ļ�ҲҪ�ڷ����ɾ��
                    onFileDeletion(filename);
                }
            }
            //����key
            boolean valid = key.reset();
            //�����key��ʧЧ�����ɨ���б���ɾ��
            if (!valid) {
                directories.remove(key);
                //�������ɨ���κ�Ŀ¼�����˳�������ѭ������ֹ����
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
