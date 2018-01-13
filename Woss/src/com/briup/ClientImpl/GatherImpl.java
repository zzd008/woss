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
 * ���ݲɼ�
 */
public class GatherImpl implements Gather,WossModule{
	
	private String fileName;
	
	public void init(Properties arg0) {
		this.fileName=arg0.getProperty("resource_path");
	}

	/*��ȡ��־�ļ�������ip���������¼7,8
	 * ����һ��������BIDR���󣬷ŵ�������ȥ
	 * ����һ���ļ�һ��Сʱ�ɼ�һ��
	 * ��������ɼ���ʱ���б��Ϊ7���û�û������
	 */
	public Collection<BIDR> gather() throws Exception {
		int i=0;
		ArrayList<BIDR> list=new ArrayList<BIDR>();
		Map<String,BIDR> map=new HashMap<String,BIDR>();
		//��ű�Ƕ���keyΪip��valueΪ����7�ͼ�1������8�ͼ�һ
		Map<String,Integer> map1=new HashMap<String,Integer>();
		BufferedReader br=new BufferedReader(new FileReader(fileName));
//		RandomAccessFile ra=new RandomAccessFile("src/com/briup/file/radwtmp", "r");
		String str;
		while((str=br.readLine())!=null){
			String[] split = str.split("\\|");
			if(split[2].equals("7")){//����
				BIDR b=new BIDR();
				b.setAAA_login_name(split[0].substring(1, split[0].length()));
				b.setLogin_date(new Timestamp(Long.parseLong(split[3])));
				b.setLogin_ip(split[4]);
				//��ȡ����ip
				b.setNAS_ip(Inet4Address.getLocalHost().toString());
				if(map.containsKey(split[4])){//��һ�ε���(��һ��7-���һ��8)
					//���������Ѿ���7
					int n=map1.get(split[4]);
					map1.put(split[4], n+1);
					i++;
				}else{//��һ������7
					map.put(split[4], b);
					map1.put(split[4], 1);
				}
			}else{//�ǳ�
				if(map.containsKey(split[4])){
					int n=map1.get(split[4]);
					n--;
					if(n==0){//Ϊ0
						BIDR b = (BIDR)map.remove(split[4]);
						b.setLogout_date(new Timestamp(Long.parseLong(split[3])));
						b.setTime_deration(new Integer(((int)(b.getLogout_date().getTime()-b.getLogin_date().getTime()))));
						list.add(b);
						map1.remove(split[4]);
					}else{//��Ϊ���һ
						map1.put(split[4], n);
					}
				}
			}
		}
		System.out.println("�ظ���¼��"+i);//�ظ������
		System.out.println("��¼δ�ǳ���"+map.size());//����δ�ǳ���
		br.close();
		return list;
	}
	
	/*
	 * ʹ��RandomAccessFile��ȡ�ļ�
	 * ģ�ⶨʱ��ȡ
	 */
	public Collection<BIDR> gather0() throws Exception {
		ArrayList<BIDR> list=new ArrayList<BIDR>();
		
		Map<String,BIDR> map=new HashMap<String,BIDR>();
		//��ű�Ƕ���keyΪip��valueΪ����7�ͼ�1������8�ͼ�һ
		Map<String,Integer> map1=new HashMap<String,Integer>();
		
		//��ȡ�ϴδ�ȡ��map��map1
		Map<Map<String, BIDR>, Map<String, Integer>> readBackMapFile = ReadBackMapFile();
		if(readBackMapFile!=null){
			for(Map<String, BIDR> key:readBackMapFile.keySet()){
				map=key;
				map1=(Map<String, Integer>) readBackMapFile.get(key);
			}
		}
		RandomAccessFile ra=new RandomAccessFile(fileName, "r");
		//��ȡƫ��������ת��ָ��λ��
		Long point = readPoint();
		ra.seek(point);
		
		String str;
		while((str=ra.readLine())!=null){
			String[] split = str.split("\\|");
			if(split[2].equals("7")){//����
				BIDR b=new BIDR();
				b.setAAA_login_name(split[0].substring(1, split[0].length()));
				b.setLogin_date(new Timestamp(Long.parseLong(split[3])));
				b.setLogin_ip(split[4]);
				b.setNAS_ip(Inet4Address.getLocalHost().toString());
				if(map.containsKey(split[4])){//��һ�ε���(��һ��7-���һ��8)
					//���������Ѿ���7
					int n=map1.get(split[4]);
					map1.put(split[4], n+1);
				}else{//��һ������7
					map.put(split[4], b);
					map1.put(split[4], 1);
				}
			}else{//�ǳ�
				if(map.containsKey(split[4])){
					int n=map1.get(split[4]);
					n--;
					if(n==0){//Ϊ0
						BIDR b = (BIDR)map.remove(split[4]);
						b.setLogout_date(new Timestamp(Long.parseLong(split[3])));
						b.setTime_deration(new Integer(((int)(b.getLogout_date().getTime()-b.getLogin_date().getTime()))));
						list.add(b);
						map1.remove(split[4]);
					}else{//��Ϊ���һ
						map1.put(split[4], n);
					}
				}
			}
		}
		
		//��ȡ�ļ�������ɺ��ƫ����
		long pointer = ra.getFilePointer();
		//����ƫ�������ļ���
		savePoint(pointer);
		
		//����û�������BIDR����ͱ�ǣ�û��ƥ�������7��
		saveBackMapFile(map,map1);
		
		ra.close();
		return list;
	}
	
	/*
	 * ����û�������BIDR����ͱ�Ǽ���
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
	 * ��ȡû�������BIDR����ͱ�Ǽ���
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
	 * ��ȡƫ����
	 */
	private Long readPoint() {
		DataInputStream dis=null;
		long point=0;
		File f=new File(fileName);
		if(f.exists()&&f.canRead()){//�ļ����ڲ��ɶ�
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

	/*����ƫ����
	 * 
	 */
	private void savePoint(long pointer) {
		DataOutputStream dos=null;
		try {
			File file=new File(fileName);
			if(!file.exists()){//�ļ�������
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
	 * ģ��ÿ����ö�һ�Σ���ȡָ������
	 * ����:�е����ظ���¼���е��˵�¼����û�еǳ�
	 * ģ�⣺ÿ�ζ�readLines�У���n��
	 */
	public Collection<BIDR> gather1(int readLines,int n) throws Exception {
		ArrayList BIDRList=new ArrayList<ArrayList<BIDR>>();
		ArrayList mapList=new ArrayList<HashMap<String,BIDR>>();
		//��ű�Ƕ���keyΪip��valueΪ����7�ͼ�1������8�ͼ�һ
		Map<String,Integer> map1=new HashMap<String,Integer>();
		mapList.add(new HashMap<String,BIDR>());//0-null
		for(int i=1;i<=n;i++){
			ArrayList<BIDR> list=new ArrayList<BIDR>();
			Map<String,BIDR> map=(Map<String, BIDR>) mapList.get(i-1);//��ȡ�ϴζ�ȡ���µ�map
			BufferedReader br=new BufferedReader(new FileReader(fileName));
			String str;
			int beginLine=readLines*(i-1)+1;
			int endLine=i*readLines;
			int count=1;//����
			while((str=br.readLine())!=null){
				if(count==endLine+1) break;//������
				if(count>=beginLine){//��ʼ��
					String[] split = str.split("\\|");
					if(split[2].equals("7")){//����
						BIDR b=new BIDR();
						b.setAAA_login_name(split[0].substring(1, split[0].length()));
						b.setLogin_date(new Timestamp(Long.parseLong(split[3])));
						b.setLogin_ip(split[4]);
						b.setNAS_ip(Inet4Address.getLocalHost().toString());
						if(map.containsKey(split[4])){//��һ�ε���(��һ��7-���һ��8)
							//���������Ѿ���7
							int n1=map1.get(split[4]);
							map1.put(split[4], n1+1);
						}else{//��һ������7
							map.put(split[4], b);
							map1.put(split[4], 1);
						}
					}else{//�ǳ�
						if(map.containsKey(split[4])){
							int n1=map1.get(split[4]);
							n1--;
							if(n1==0){//Ϊ0
								BIDR b = (BIDR)map.remove(split[4]);
								b.setLogout_date(new Timestamp(Long.parseLong(split[3])));
								b.setTime_deration(new Integer(((int)(b.getLogout_date().getTime()-b.getLogin_date().getTime()))));
								list.add(b);
								map1.remove(split[4]);
							}else{//��Ϊ���һ
								map1.put(split[4], n1);
							}
						}
					}
				}
				count++;
			}
			mapList.add(map);
			BIDRList.add(list);//ÿ�ζ�ȡ���󼯺ϴ��뼯��
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
			System.out.println("��¼�ǳ�"+list.size());
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
			System.out.println("��¼�ǳ�"+list.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
