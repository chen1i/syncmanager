package com.download.server;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * @author user
 *
 */
public class BaseDAO {
	protected PreparedStatement ps;//protected��ͬ��������
	protected PreparedStatement ps1;//protected��ͬ��������
	protected ResultSet rs;
	protected ResultSet rs1;

	// �����������
	protected Connection getConn(){
		return DBHelper.getConn();
	}
	
	// �ر����ݿ�����
	protected void closeConn(Connection conn){
		DBHelper.closeConn(conn);
	}
	/***********************
	 * 
	 * @param rs
	 * @param stmt
	 * @���� �ر�������Դ
	 */
	protected void closeResource() {
		// �ر����ݼ��϶���
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		}
		// �ر�����statement����
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
	}
}
