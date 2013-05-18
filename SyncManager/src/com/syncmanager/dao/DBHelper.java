package com.syncmanager.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created with Eclipse
 * User: Gu Junfeng
 * Date: 13-5-18
 * Time: ����10:53
 */
public class DBHelper {

    // ���ݿ�����
    private static final String driver = "com.mysql.jdbc.Driver";
    // ���ݿ�URL
    private static final String url = "jdbc:mysql://127.0.0.1:3306/download";
    // ���ݿ��û���
    private static final String username = "root";
    // ���ݿ�����
    private static final String pwd = "root";
    // ���ݿ�����
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

    // �������ݿ�����
    public static void main(String[] args) {
        Connection conn = getConn();
        if (conn != null) {
            System.out.println("���ӳɹ�");
        } else {
            System.out.println("����ʧ��");
        }
    }


    // �ر����ݿ�����
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
