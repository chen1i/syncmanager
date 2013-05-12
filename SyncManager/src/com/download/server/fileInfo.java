package com.download.server;

import java.net.URLEncoder;

public class fileInfo {
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
	private String filepath;
	/**
	 * 所属用户
	 */
	private String username;
	/**
	 * 最大上传大小
	 */
	private String maxsize;
	/**
	 * 原有的地址
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
