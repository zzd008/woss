package com.briup.ServerImpl;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.sql.PreparedStatement;

import com.briup.Util.BackUpImpl;
import com.briup.Util.DBUtil;
import com.briup.Util.LoggerImpl;
import com.briup.util.BIDR;
import com.briup.woss.WossModule;
import com.briup.woss.server.DBStore;

/*
 * 服务端将接受到的数据存入数据库
 */
public class DBStoreImpl implements DBStore,WossModule{
	
	private String backdb_path;

	public void init(Properties arg0) {
		backdb_path=arg0.getProperty("backdb_path");
	}
	
	private static int flag=2;
	static{
		flag=0;
	}

	public void saveToDB(Collection<BIDR> c) throws Exception {
		int i=0;
		int count=0;
		ArrayList list=(ArrayList)c;
		try {
			Connection con = DBUtil.getConnection();
			String sql="insert into  t_detail_1 values(?,?,?,?,?,?);";
			PreparedStatement ps = con.prepareStatement(sql);
			for(;i<list.size();i++){
				BIDR b=(BIDR)(list.get(i));
				ps.setString(1, b.getAAA_login_name());
				ps.setString(2, b.getLogin_ip());
				ps.setDate(3, new Date(b.getLogin_date().getTime()));
				ps.setDate(4, new Date(b.getLogout_date().getTime()));
				ps.setString(5, b.getNAS_ip());
				ps.setInt(6, b.getTime_deration());
				ps.addBatch();
				if(i%3000==0){
					ps.executeBatch();
					count+=3000;
				}
				//模拟报错
				//让错误只发生一次
				/*if(i==1000){
					if(flag==0){
						System.out.println("发生错误！！！");
						flag=1;
						int i1=1/0;
					}
				}	*/
			}
			ps.executeBatch();
			System.out.println("入库成功！");
		} catch (Exception e) {
			i=count;
			System.out.println("出现错误,进行文件备份！");
			System.out.println("读入数据"+count+"条");
			//出现错误，将后面的对象集合保存到文件中去
			BackUpImpl bui=new BackUpImpl();
			list.subList(i, list.size());
//			bui.store("DBBIDRBack", list, true);
			bui.store(backdb_path, list, true);
			e.printStackTrace();
			new LoggerImpl().error(e.getMessage());
			
			//入库时报错，重新从备份文件中加载 
			/*ArrayList backlist=(ArrayList) bui.load("DBBIDRBack", true);
			System.out.println(backlist.size());
			System.out.println("重新入库--------------------");
			saveToDB(backlist);	
			System.out.println("ok!");*/
		}
	}

}
