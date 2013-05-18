package com.syncmanager.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created with Eclipse
 * User: Gu Junfeng
 * Date: 13-5-18
 * Time: 上午10:53
 */
public class DBHelper {

    // 数据库驱动
    private static final String driver = "com.mysql.jdbc.Driver";
    // 数据库URL
    private static final String url = "jdbc:mysql://127.0.0.1:3306/download";
    // 数据库用户名
    private static final String username = "root";
    // 数据库密码
    private static final String pwd = "root";
    // 数据库连接
    private static Connection conn;

    public static Connection getConn() {
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, pwd);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    // 测试数据库连接
    public static void main(String[] args) {
        Connection conn = getConn();
        if (conn != null) {
            System.out.println("连接成功");
        } else {
            System.out.println("连接失败");
        }
    }


    // 关闭数据库连接
    public static void closeConn(Connection conn) {
        if (null != conn) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
