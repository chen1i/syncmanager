package com.download.server;

import java.net.URLEncoder;

public class fileInfo {
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
	private String filepath;
	/**
	 * �����û�
	 */
	private String username;
	/**
	 * ����ϴ���С
	 */
	private String maxsize;
	/**
	 * ԭ�еĵ�ַ
	 */
	private String oldpath;
	

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

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
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

	public String getOldpath() {
		return oldpath;
	}

	public void setOldpath(String oldpath) {
		this.oldpath = oldpath;
	}
}
