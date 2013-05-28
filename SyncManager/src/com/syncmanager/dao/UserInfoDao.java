package com.syncmanager.dao;

import com.syncmanager.dao.orm.UserInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with Eclipse
 * User: Gu Junfeng
 * Date: 13-5-18
 * Time: ÉÏÎç10:59
 */
public class UserInfoDao extends BaseDAO {
    public boolean saveUserInfo(UserInfo info) {
        boolean isflat = false;
        String sql = "insert into UserInfo(username,userRealName,userclass,scroe,remark) "
                + "values(?,?,?,?,?)";
        Connection conn = this.getConn();
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, info.getUsername());
            ps.setString(2, info.getUserRealName());
            ps.setString(3, info.getUserclass());
            ps.setInt(4, info.getScroe());
            ps.setString(5, info.getRemark());
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

    public boolean updateuserInfo(UserInfo info) {
        boolean isflat = false;
        String sql = "update UserInfo set username=?,userRealName=?,userclass=?,scroe=?,remark=? where id=?";
        Connection conn = this.getConn();
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, info.getUsername());
            ps.setString(2, info.getUserRealName());
            ps.setString(3, info.getUserclass());
            ps.setInt(4, info.getScroe());
            ps.setString(5, info.getRemark());
            ps.setInt(6, info.getId());
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

    public List getAlluserInfo() {
        List list = new ArrayList();
        String sql = "select * from UserInfo";
        Connection conn = getConn();
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                UserInfo info = new UserInfo();
                info.setId(rs.getInt("id"));
                info.setUsername(rs.getString("username"));
                info.setUserRealName(rs.getString("userRealName"));
                info.setUserclass(rs.getString("userclass"));
                info.setScroe(rs.getInt("scroe"));
                info.setRemark(rs.getString("remark"));
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

    public List getAlluserInfo(String userName) {
        List list = new ArrayList();
        String sql = "select * from UserInfo where username like '%"+userName+"%'";
        Connection conn = getConn();
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                UserInfo info = new UserInfo();
                info.setId(rs.getInt("id"));
                info.setUsername(rs.getString("username"));
                info.setUserRealName(rs.getString("userRealName"));
                info.setUserclass(rs.getString("userclass"));
                info.setScroe(rs.getInt("scroe"));
                info.setRemark(rs.getString("remark"));
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

    public UserInfo getAlluserInfoByUserName(String userName) {
        UserInfo info=null;
        String sql = "select * from UserInfo where username = '"+userName+"'";
        Connection conn = getConn();
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                info = new UserInfo();
                info.setId(rs.getInt("id"));
                info.setUsername(rs.getString("username"));
                info.setUserRealName(rs.getString("userRealName"));
                info.setUserclass(rs.getString("userclass"));
                info.setScroe(rs.getInt("scroe"));
                info.setRemark(rs.getString("remark"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResource();
            this.closeConn(conn);
        }
        return info;
    }

    public UserInfo getuserInfoById(int id) {
        String sql = "select * from UserInfo where id=" + id;
        Connection conn = getConn();
        UserInfo info = null;
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                info = new UserInfo();
                info.setId(rs.getInt("id"));
                info.setUsername(rs.getString("username"));
                info.setUserRealName(rs.getString("userRealName"));
                info.setUserclass(rs.getString("userclass"));
                info.setScroe(rs.getInt("scroe"));
                info.setRemark(rs.getString("remark"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResource();
            this.closeConn(conn);
        }
        return info;
    }
    public UserInfo deleteUserInfoById(int id) {
        String sql = "delete from UserInfo where id=" + id;
        Connection conn = getConn();
        UserInfo info = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResource();
            this.closeConn(conn);
        }
        return info;
    }

    public UserInfo getAlluserInfoByUserNameAndPassword(String userName, String password, String type) {
        UserInfo info=null;
        String sql = "select * from UserInfo where username = '"+userName+"' and userRealName='"+password+"' and remark='"+type+"'";
        Connection conn = getConn();
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                info = new UserInfo();
                info.setId(rs.getInt("id"));
                info.setUsername(rs.getString("username"));
                info.setUserRealName(rs.getString("userRealName"));
                info.setUserclass(rs.getString("userclass"));
                info.setScroe(rs.getInt("scroe"));
                info.setRemark(rs.getString("remark"));
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
