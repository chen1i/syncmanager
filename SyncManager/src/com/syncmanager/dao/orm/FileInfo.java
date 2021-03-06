package com.syncmanager.dao.orm;

/**
 * Created with Eclipse
 * User: Gu Junfeng
 * Date: 13-5-18
 * Time: 上午10:46
 */
public class FileInfo {
    /**
     * 自动编号ID
     */
    private int id;
    /**
     * 文件名称
     */
    private String filename;
    /**
     * 文件大小
     */
    private String filesize;
    /**
     * 文件的版本
     */
    private String version;
    /**
     * 文件的生成时间
     */
    private String createdate;
    /**
     * 文件的路径
     */
    private String fileurl;
    /**
     * 所属用户
     */
    private String username;
    /**
     * 最大上传大小
     */
    private String maxsize;
    /**
     * 存储地址
     */
    private String storepath;

    /**
     * 原有的地址
     */
    private String origPath;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMaxsize() {
        return maxsize;
    }

    public void setMaxsize(String maxsize) {
        this.maxsize = maxsize;
    }

    public String getStorepath() {
        return storepath;
    }

    public void setStorepath(String storepath) {
        this.storepath = storepath;
    }

    public String getOrigPath() {
        return origPath;
    }

    public void setOrigPath(String origPath) {
        this.origPath = origPath;
    }
}
