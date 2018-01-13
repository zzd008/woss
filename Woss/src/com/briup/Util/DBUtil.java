package com.briup.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.junit.Test;


public class DBUtil {
	private static String driverurl;
	private static String url;
	private static String name;
	private static String password;
	
	static{
		ResourceBundle res=ResourceBundle.getBundle("Dbinfo");
		driverurl=res.getString("driverurl");
		url=res.getString("url");
		name=res.getString("name");
		password=res.getString("password");
		try {
			Class.forName(driverurl);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static Connection getConnection() throws SQLException{
		return DriverManager.getConnection(url,name,password);
	}
	public static void clossAll(Connection c,PreparedStatement p,ResultSet r) throws SQLException{
		if(r!=null){
			r.close();
		}else{
			r=null;
		}
		if(p!=null){
			p.close();
		}else{
			p=null;
		}
		if(c!=null){
			c.close();
		}else{
			c=null;
		}
	}
	@Test
	public void text1() throws SQLException{
		if(DBUtil.getConnection()!=null){
			System.out.println("数据库连接正常！");
		}
		Connection c=DBUtil.getConnection();
		DBUtil.clossAll(c, null, null);
		System.out.println("数据库关闭正常！");
	}
	
}

