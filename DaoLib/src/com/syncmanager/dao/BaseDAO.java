package com.syncmanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with Eclipse
 * User: Gu Junfeng
 * Date: 13-5-18
 * Time: 上午10:52
 */
public class BaseDAO {
    protected PreparedStatement ps;//protected：同包和子类
    protected PreparedStatement ps1;//protected：同包和子类
    protected ResultSet rs;
    protected ResultSet rs1;

    // 获得数据连接
    protected Connection getConn(){
        return DBHelper.getConn();
    }

    // 关闭数据库连接
    protected void closeConn(Connection conn){
        DBHelper.closeConn(conn);
    }
    /***********************
     *
     * @param rs
     * @param stmt
     * @功能 关闭数据资源
     */
    protected void closeResource() {
        // 关闭数据集合对象
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {

                e.printStackTrace();
            }
        }
        // 关闭数据statement对象
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {

                e.printStackTrace();
            }
        }
    }
}
