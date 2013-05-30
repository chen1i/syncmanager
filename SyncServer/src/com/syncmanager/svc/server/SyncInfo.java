package com.syncmanager.svc.server;

import java.util.Scanner;

/**
 * Created with Eclipse
 * User: Gu Junfeng
 * Date: 13-5-30
 * Time: ÉÏÎç6:08
 */
public class SyncInfo {
    public Activity getAct() {
        return act;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getFileTime() {
        return fileTime;
    }

    public enum Activity {
        Create, Modify, Delete
    }

    private Activity act;
    private String   fileName;
    private long   fileSize;
    private String fileTime;

    public SyncInfo(String cmd) {
        Scanner scanner = new Scanner(cmd);
        String a = scanner.nextLine();
        if (a.equals("C"))
            act = Activity.Create;
        else if (a.equals("M"))
            act = Activity.Modify;
        else if (a.equals("D"))
            act = Activity.Delete;
        else
            throw new RuntimeException("Unsupported Activity.");

        fileName = scanner.nextLine();
        fileSize = Long.parseLong(scanner.nextLine());
        fileTime = scanner.nextLine();
    }
}
