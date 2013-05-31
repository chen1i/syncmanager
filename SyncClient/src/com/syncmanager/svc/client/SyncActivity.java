package com.syncmanager.svc.client;

import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;

/**
 * Created with Eclipse
 * User: Gu Junfeng
 * Date: 13-5-30
 * Time: ÉÏÎç4:46
 */
public class SyncActivity {
    public enum Activity {
        Create, Modify, Delete
    }

    private String owner;
    private String act;
    private File file;
    public SyncActivity(String login, Path filename, Activity event) {
        owner = login;
        file = filename.toFile();
        if (event == Activity.Create)
            act = "C";
        else if (event == Activity.Modify)
            act = "M";
        else if (event == Activity.Delete)
            act = "D";
        else
            act = "N";
    }

    public String toCmdString() {
        StringBuilder sb = new StringBuilder();
        sb.append(act).append('\n');
        sb.append(owner).append('\n');
        sb.append(file.getAbsolutePath()).append('\n');
        sb.append(file.length()).append('\n');
        sb.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.lastModified())).append('\n');

        return sb.toString();
    }
}
