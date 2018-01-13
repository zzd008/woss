package com.briup.ClientImpl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import com.briup.util.BIDR;
import com.briup.woss.WossModule;
import com.briup.woss.client.Gather;

/*
 * 数据采集
 */
public class GatherImpl implements Gather,WossModule{
	
	private String fileName;
	
	public void init(Properties arg0) {
		this.fileName=arg0.getProperty("resource_path");
	}

	/*读取日志文件，基于ip获得两条记录7,8
	 * 生成一个完整的BIDR对象，放到集合中去
	 * 问题一：文件一个小时采集一次
	 * 问题二：采集的时候还有标记为7的用户没有下线
	 */
	public Collection<BIDR> gather() throws Exception {
		int i=0;
		ArrayList<BIDR> list=new ArrayList<BIDR>();
		Map<String,BIDR> map=new HashMap<String,BIDR>();
		//存放标记对象，key为ip，value为遇见7就加1，遇见8就减一
		Map<String,Integer> map1=new HashMap<String,Integer>();
		BufferedReader br=new BufferedReader(new FileReader(fileName));
//		RandomAccessFile ra=new RandomAccessFile("src/com/briup/file/radwtmp", "r");
		String str;
		while((str=br.readLine())!=null){
			String[] split = str.split("\\|");
			if(split[2].equals("7")){//登入
				BIDR b=new BIDR();
				b.setAAA_login_name(split[0].substring(1, split[0].length()));
				b.setLogin_date(new Timestamp(Long.parseLong(split[3])));
				b.setLogin_ip(split[4]);
				//获取本机ip
				b.setNAS_ip(Inet4Address.getLocalHost().toString());
				if(map.containsKey(split[4])){//第一次登入(第一个7-最后一个8)
					//集合里面已经有7
					int n=map1.get(split[4]);
					map1.put(split[4], n+1);
					i++;
				}else{//第一次遇见7
					map.put(split[4], b);
					map1.put(split[4], 1);
				}
			}else{//登出
				if(map.containsKey(split[4])){
					int n=map1.get(split[4]);
					n--;
					if(n==0){//为0
						BIDR b = (BIDR)map.remove(split[4]);
						b.setLogout_date(new Timestamp(Long.parseLong(split[3])));
						b.setTime_deration(new Integer(((int)(b.getLogout_date().getTime()-b.getLogin_date().getTime()))));
						list.add(b);
						map1.remove(split[4]);
					}else{//不为零减一
						map1.put(split[4], n);
					}
				}
			}
		}
		System.out.println("重复登录："+i);//重复登入的
		System.out.println("登录未登出："+map.size());//登入未登出的
		br.close();
		return list;
	}
	
	/*
	 * 使用RandomAccessFile读取文件
	 * 模拟定时读取
	 */
	public Collection<BIDR> gather0() throws Exception {
		ArrayList<BIDR> list=new ArrayList<BIDR>();
		
		Map<String,BIDR> map=new HashMap<String,BIDR>();
		//存放标记对象，key为ip，value为遇见7就加1，遇见8就减一
		Map<String,Integer> map1=new HashMap<String,Integer>();
		
		//读取上次存取的map、map1
		Map<Map<String, BIDR>, Map<String, Integer>> readBackMapFile = ReadBackMapFile();
		if(readBackMapFile!=null){
			for(Map<String, BIDR> key:readBackMapFile.keySet()){
				map=key;
				map1=(Map<String, Integer>) readBackMapFile.get(key);
			}
		}
		RandomAccessFile ra=new RandomAccessFile(fileName, "r");
		//读取偏移量，跳转到指定位置
		Long point = readPoint();
		ra.seek(point);
		
		String str;
		while((str=ra.readLine())!=null){
			String[] split = str.split("\\|");
			if(split[2].equals("7")){//登入
				BIDR b=new BIDR();
				b.setAAA_login_name(split[0].substring(1, split[0].length()));
				b.setLogin_date(new Timestamp(Long.parseLong(split[3])));
				b.setLogin_ip(split[4]);
				b.setNAS_ip(Inet4Address.getLocalHost().toString());
				if(map.containsKey(split[4])){//第一次登入(第一个7-最后一个8)
					//集合里面已经有7
					int n=map1.get(split[4]);
					map1.put(split[4], n+1);
				}else{//第一次遇见7
					map.put(split[4], b);
					map1.put(split[4], 1);
				}
			}else{//登出
				if(map.containsKey(split[4])){
					int n=map1.get(split[4]);
					n--;
					if(n==0){//为0
						BIDR b = (BIDR)map.remove(split[4]);
						b.setLogout_date(new Timestamp(Long.parseLong(split[3])));
						b.setTime_deration(new Integer(((int)(b.getLogout_date().getTime()-b.getLogin_date().getTime()))));
						list.add(b);
						map1.remove(split[4]);
					}else{//不为零减一
						map1.put(split[4], n);
					}
				}
			}
		}
		
		//获取文件解析完成后的偏移量
		long pointer = ra.getFilePointer();
		//保存偏移量到文件中
		savePoint(pointer);
		
		//保存没有用完的BIDR对象和标记（没有匹配的所有7）
		saveBackMapFile(map,map1);
		
		ra.close();
		return list;
	}
	
	/*
	 * 保存没有用完的BIDR对象和标记集合
	 */
	private void saveBackMapFile(Map<String, BIDR> map,Map<String, Integer> map1){
		ObjectOutputStream oos=null;
		try {
			Map<Map<String, BIDR>,Map<String, Integer>> m=new HashMap<Map<String, BIDR>,Map<String, Integer>>();
			m.put(map, map1);
			File f=new File(fileName);
			if(!f.exists()){
				f.createNewFile();
			}
			oos=new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(m);
			oos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(oos!=null){
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/*
	 * 读取没有用完的BIDR对象和标记集合
	 */
	private Map<Map<String, BIDR>,Map<String, Integer>> ReadBackMapFile(){
		ObjectInputStream oos=null;
		Map<Map<String, BIDR>,Map<String, Integer>> m=null;
		try {
			File f=new File(fileName);
			if(f.exists()&&f.canRead()){
				oos=new ObjectInputStream(new FileInputStream(f));
				try {
					m=(Map)oos.readObject();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(oos!=null){
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return m;
	}

	/*
	 * 读取偏移量
	 */
	private Long readPoint() {
		DataInputStream dis=null;
		long point=0;
		File f=new File(fileName);
		if(f.exists()&&f.canRead()){//文件存在并可读
			try {
				dis=new DataInputStream(new FileInputStream(f));
				try {
					point=dis.readLong();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}finally{
				if(dis!=null){
					try {
						dis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return point;
	}

	/*保存偏移量
	 * 
	 */
	private void savePoint(long pointer) {
		DataOutputStream dos=null;
		try {
			File file=new File(fileName);
			if(!file.exists()){//文件不存在
				file.createNewFile();
			}
			dos=new DataOutputStream(new FileOutputStream(file));
			dos.writeLong(pointer);
			dos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(dos!=null){
				try {
					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	/*
	 * 模拟每隔多久读一次，读取指定行数
	 * 问题:有的人重复登录，有的人登录但还没有登出
	 * 模拟：每次读readLines行，读n次
	 */
	public Collection<BIDR> gather1(int readLines,int n) throws Exception {
		ArrayList BIDRList=new ArrayList<ArrayList<BIDR>>();
		ArrayList mapList=new ArrayList<HashMap<String,BIDR>>();
		//存放标记对象，key为ip，value为遇见7就加1，遇见8就减一
		Map<String,Integer> map1=new HashMap<String,Integer>();
		mapList.add(new HashMap<String,BIDR>());//0-null
		for(int i=1;i<=n;i++){
			ArrayList<BIDR> list=new ArrayList<BIDR>();
			Map<String,BIDR> map=(Map<String, BIDR>) mapList.get(i-1);//获取上次读取存下的map
			BufferedReader br=new BufferedReader(new FileReader(fileName));
			String str;
			int beginLine=readLines*(i-1)+1;
			int endLine=i*readLines;
			int count=1;//计数
			while((str=br.readLine())!=null){
				if(count==endLine+1) break;//结束读
				if(count>=beginLine){//开始读
					String[] split = str.split("\\|");
					if(split[2].equals("7")){//登入
						BIDR b=new BIDR();
						b.setAAA_login_name(split[0].substring(1, split[0].length()));
						b.setLogin_date(new Timestamp(Long.parseLong(split[3])));
						b.setLogin_ip(split[4]);
						b.setNAS_ip(Inet4Address.getLocalHost().toString());
						if(map.containsKey(split[4])){//第一次登入(第一个7-最后一个8)
							//集合里面已经有7
							int n1=map1.get(split[4]);
							map1.put(split[4], n1+1);
						}else{//第一次遇见7
							map.put(split[4], b);
							map1.put(split[4], 1);
						}
					}else{//登出
						if(map.containsKey(split[4])){
							int n1=map1.get(split[4]);
							n1--;
							if(n1==0){//为0
								BIDR b = (BIDR)map.remove(split[4]);
								b.setLogout_date(new Timestamp(Long.parseLong(split[3])));
								b.setTime_deration(new Integer(((int)(b.getLogout_date().getTime()-b.getLogin_date().getTime()))));
								list.add(b);
								map1.remove(split[4]);
							}else{//不为零减一
								map1.put(split[4], n1);
							}
						}
					}
				}
				count++;
			}
			mapList.add(map);
			BIDRList.add(list);//每次读取对象集合存入集合
			br.close();
		}
		return BIDRList;
	}
	
	@Test
	public void test1() throws Exception{
		try {
			ArrayList<BIDR> list=(ArrayList<BIDR>) new GatherImpl().gather();
//			BIDR bidr = list.get(0);
//			System.out.println(bidr.getAAA_login_name()+" "+bidr.getTime_deration());
			System.out.println("登录登出"+list.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test2() throws Exception{
		try {
			ArrayList list=(ArrayList)new GatherImpl().gather1(10436,3);
			for(int i=0;i<list.size();i++){
				ArrayList list1=(ArrayList)list.get(i);
				System.out.println(list1.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test3() throws Exception{
		try {
			ArrayList<BIDR> list=(ArrayList<BIDR>) new GatherImpl().gather0();
			System.out.println("登录登出"+list.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
