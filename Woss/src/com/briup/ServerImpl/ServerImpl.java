package com.briup.ServerImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.junit.Test;

import com.briup.ClientImpl.ClientImpl;
import com.briup.ClientImpl.GatherImpl;
import com.briup.Util.BackUpImpl;
import com.briup.Util.LoggerImpl;
import com.briup.util.BIDR;
import com.briup.woss.WossModule;
import com.briup.woss.server.Server;

/*
 * ����˽��ܿͻ��˷��͹���������
 */
public class ServerImpl implements Server,WossModule{
	
	private int port;
	private String backserver_path;
	
	private boolean stop=true;
	
	public void init(Properties arg0) {
		port=Integer.parseInt(arg0.getProperty("port"));
		backserver_path=arg0.getProperty("backserver_path");
	}
	
	public  Collection<BIDR> revicer() throws Exception {
		ServerSocket sc=new ServerSocket(port);
		while(this.stop){
			Socket socket = sc.accept();
			//���߳����
			new Thread(new ServerRunable(sc,socket)).start();
		}
		return null;
	}

	class ServerRunable implements Runnable{
		int flag=0;
		private ServerSocket sc;
		private Socket socket;
		public void run() {
			try {
				InputStream in = socket.getInputStream();
				ObjectInputStream ois=new ObjectInputStream(in);
				
				OutputStream os = socket.getOutputStream();
				PrintWriter pw=new PrintWriter(os);
				
				ArrayList list=null;
				list=(ArrayList) ois.readObject();
				if(list!=null){
					ArrayList list1=(ArrayList)list.get(0);
					ArrayList list2=(ArrayList)list.get(1);
					if(list1.size()==(Integer)list2.get(0)){
						//���߿ͻ���
						pw.println("yes");
						pw.flush();
						
						System.out.println("�����ѽ���,����δ��ʧ��");
						System.out.println(list1.size());
						//���
						try {
							//�������ݵļ��Ϻʹ����ļ��Ϻϲ� һ��д�����ݿ�
							BackUpImpl bui=new BackUpImpl();
							ArrayList backlist=(ArrayList) bui.load(backserver_path, true);
							if(backlist!=null){
								list1.addAll(list1.size(), backlist);
							}
							System.out.println("�ܳ��ȣ�"+list1.size());
							System.out.println("�������--------------------");
							new DBStoreImpl().saveToDB(list1);
						} catch (Exception e) {
							new LoggerImpl().error(e.getMessage());
						}
					}else{
						//���ݶ�ʧ
						pw.println("no");
						pw.flush();
					}
					
				}
		
			} catch (ClassNotFoundException e) {
				new LoggerImpl().error(e.getMessage());
			} catch (IOException e) {
				new LoggerImpl().error(e.getMessage());
			}
		}
		public ServerRunable(ServerSocket sc, Socket socket) {
			this.sc = sc;
			this.socket = socket;
		}
		
	}
	
	/*
	 * �رս���
	 */
	public void shutdown() {
		this.stop=false;
	}

	public static void main(String[] args) {
		try {
			new ServerImpl().revicer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test1() throws Exception{
		try {
			ArrayList l1=new ArrayList();
			l1.add(1);
			l1.add(2);
			l1.add(3);
			ArrayList l2=new ArrayList();
			l2.add(1);
			l2.addAll(1, l1);
			System.out.println(l2.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
