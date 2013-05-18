package com.download.server;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class FileInfoDao extends BaseDAO {
	public boolean savefileInfo(FileInfo info) {
		boolean isflat = false;
		String sql = "insert into fileInfo(filename,filesize,version,createdate,filepath,username,oldpath) "
				+ "values(?,?,?,?,?,?,?)";
		Connection conn = this.getConn();
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, info.getFilename());
			ps.setString(2, info.getFilesize());
			ps.setString(3, info.getVersion());
			ps.setString(4, info.getCreatedate());
			ps.setString(5, info.getFilepath());
			ps.setString(6, info.getUsername());
			ps.setString(7, info.getOldpath());
			int count = ps.executeUpdate();
			if (count > 0) {
				isflat = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResource();
			this.closeConn(conn);
		}
		return isflat;
	}

	public boolean updatefileInfo(FileInfo info) {
		boolean isflat = false;
		String sql = "update fileInfo set version=?,maxsize=?,filesize=? where id=?";
		Connection conn = this.getConn();
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, info.getVersion());
			ps.setString(2, info.getMaxsize());
			ps.setString(3, info.getFilesize());
			ps.setInt(4, info.getId());
			System.out.println(sql);
			int count = ps.executeUpdate();
			if (count > 0) {
				isflat = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResource();
			this.closeConn(conn);
		}
		return isflat;
	}
	
	public List getAllfileInfo() {
		List list = new ArrayList();
		String sql = "select * from fileInfo";
		Connection conn = getConn();
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				FileInfo info = new FileInfo();
				info.setId(rs.getInt("id"));
				info.setFilename(rs.getString("filename"));
				info.setFilesize(rs.getString("filesize"));
				info.setVersion(rs.getString("version"));
				info.setCreatedate(rs.getString("createdate"));
				info.setFilepath(rs.getString("filepath"));
				info.setUsername(rs.getString("username"));
				info.setMaxsize(rs.getString("maxsize"));
				info.setOldpath(rs.getString("oldpath"));
				list.add(info);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResource();
			this.closeConn(conn);
		}
		return list;
	}

	public List getAllfileInfoByUserName(String userName) {
		List list = new ArrayList();
		String sql = "select * from fileInfo where username='"+userName+"'";
		Connection conn = getConn();
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				FileInfo info = new FileInfo();
				info.setId(rs.getInt("id"));
				info.setFilename(rs.getString("filename"));
				info.setFilesize(rs.getString("filesize"));
				info.setVersion(rs.getString("version"));
				info.setCreatedate(rs.getString("createdate"));
				info.setFilepath(rs.getString("filepath"));
				info.setUsername(rs.getString("username"));
				info.setMaxsize(rs.getString("maxsize"));
				info.setOldpath(rs.getString("oldpath"));
				list.add(info);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResource();
			this.closeConn(conn);
		}
		return list;
	}
	
	public FileInfo getfileInfoById(int id) {
		String sql = "select * from fileInfo where id=" + id;
		Connection conn = getConn();
		FileInfo info = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				info = new FileInfo();
				info.setId(rs.getInt("id"));
				info.setFilename(rs.getString("filename"));
				info.setFilesize(rs.getString("filesize"));
				info.setVersion(rs.getString("version"));
				info.setCreatedate(rs.getString("createdate"));
				info.setFilepath(rs.getString("filepath"));
				info.setUsername(rs.getString("username"));
				info.setMaxsize(rs.getString("maxsize"));
				info.setOldpath(rs.getString("oldpath"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResource();
			this.closeConn(conn);
		}
		return info;
	}
	
	public FileInfo getfileInfoByFileName(String fileName) {
		String sql = "select * from fileInfo where filename='"+fileName+"'";
		Connection conn = getConn();
		FileInfo info = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				info = new FileInfo();
				info.setId(rs.getInt("id"));
				info.setFilename(rs.getString("filename"));
				info.setFilesize(rs.getString("filesize"));
				info.setVersion(rs.getString("version"));
				info.setCreatedate(rs.getString("createdate"));
				info.setFilepath(rs.getString("filepath"));
				info.setUsername(rs.getString("username"));
				info.setMaxsize(rs.getString("maxsize"));
				info.setOldpath(rs.getString("oldpath"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResource();
			this.closeConn(conn);
		}
		return info;
	}
}
