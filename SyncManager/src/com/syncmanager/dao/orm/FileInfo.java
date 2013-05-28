package com.syncmanager.dao.orm;

/**
 * Created with Eclipse
 * User: Gu Junfeng
 * Date: 13-5-18
 * Time: ����10:46
 */
public class FileInfo {
    /**
     * �Զ����ID
     */
    private int id;
    /**
     * �ļ�����
     */
    private String filename;
    /**
     * �ļ���С
     */
    private String filesize;
    /**
     * �ļ��İ汾
     */
    private String version;
    /**
     * �ļ�������ʱ��
     */
    private String createdate;
    /**
     * �ļ���·��
     */
    private String fileurl;
    /**
     * �����û�
     */
    private String username;
    /**
     * ����ϴ���С
     */
    private String maxsize;
    /**
     * �洢��ַ
     */
    private String storepath;

    /**
     * ԭ�еĵ�ַ
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
