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
        System.out.println("��ʼ��ʱɨ��Ŀ¼... ");
        try {
            watchRNDir(start_folder);
        } catch (IOException ex){
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

    private void watchRNDir(Path start) throws IOException, InterruptedException {
        watchService = FileSystems.getDefault().newWatchService();
        registerTree(start);
        //������������ѭ��ģʽ
        while (true) {
            //ȡ��һ���иĶ����¼�key
            final WatchKey key = watchService.take();
            //�������е��¼�
            for (WatchEvent<?> watchEvent : key.pollEvents()) {
                //�õ��¼����ͣ��������Ķ���ɾ����
                final WatchEvent.Kind<?> kind = watchEvent.kind();
                //�¼��������ļ�
                final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
                final Path filename = watchEventPath.context();
                //���Թ�ʱ���¼�
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }
                //���ڴ����ļ����¼���������ļ��Ǹ�Ŀ¼����Ҫ������¼ӵ�Ŀ¼Ҳ�ӵ�ɨ���б���
                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    final Path directory_path = directories.get(key);
                    final Path child = directory_path.resolve(filename);
                    if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                        registerTree(child);
                    }
                }

                //������ʾ�������иĶ����ļ�����������
                String activity = kind.toString() + " -> " + filename;
                System.out.println(activity);
                comm.send_file_to_server(activity);

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
        }
        watchService.close();
    }
}
