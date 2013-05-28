package com.syncmanager.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.syncmanager.dao.orm.FileInfo;

/**
 * Created with Eclipse
 * User: Gu Junfeng
 * Date: 13-5-18
 * Time: ÉÏÎç10:59
 */
public class FileInfoDao extends BaseDAO {
    public boolean savefileInfo(FileInfo info) {
        boolean isflat = false;
        String sql = "insert into fileInfo(filename,filesize,version,createdate,fileurl,username,storepath,origpath) "
                + "values(?,?,?,?,?,?,?,?)";
        Connection conn = this.getConn();
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, info.getFilename());
            ps.setString(2, info.getFilesize());
            ps.setString(3, info.getVersion());
            ps.setString(4, info.getCreatedate());
            ps.setString(5, info.getFileurl());
            ps.setString(6, info.getUsername());
            ps.setString(7, info.getStorepath());
            ps.setString(8, info.getOrigPath());
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
                FileInfo info = createFileInfoObj(rs);
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
                FileInfo info = createFileInfoObj(rs);
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
                info = createFileInfoObj(rs);
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
                info = createFileInfoObj(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResource();
            this.closeConn(conn);
        }
        return info;
    }

    private FileInfo createFileInfoObj(ResultSet rs) throws SQLException {
        FileInfo info;
        info = new FileInfo();
        info.setId(rs.getInt("id"));
        info.setFilename(rs.getString("filename"));
        info.setFilesize(rs.getString("filesize"));
        info.setVersion(rs.getString("version"));
        info.setCreatedate(rs.getString("createdate"));
        info.setFileurl(rs.getString("fileurl"));
        info.setUsername(rs.getString("username"));
        info.setMaxsize(rs.getString("maxsize"));
        info.setStorepath(rs.getString("storepath"));
        info.setOrigPath(rs.getString("origpath"));
        return info;
    }
}
