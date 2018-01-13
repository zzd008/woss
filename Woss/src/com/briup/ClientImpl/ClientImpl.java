package com.briup.ClientImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.junit.Test;

import com.briup.Util.BackUpImpl;
import com.briup.Util.LoggerImpl;
import com.briup.util.BIDR;
import com.briup.woss.WossModule;
import com.briup.woss.client.Client;

/*
 * �ͻ��˷��Ͳɼ������ݵ������
 */
public class ClientImpl implements Client,WossModule{

	private String ip;
	private int port;
	private String backclient_path;
	
	public void init(Properties arg0) {
		ip=arg0.getProperty("ip");
		port=Integer.parseInt(arg0.getProperty("port"));
		backclient_path=arg0.getProperty("backclient_path");
	}

	public void send(Collection c) throws Exception {
		try {
			Socket socket=new Socket(ip, port);
			
			//����BIDR����
			OutputStream os = socket.getOutputStream();
			ObjectOutputStream oos=new ObjectOutputStream(os);
			oos.writeObject(c);
			oos.flush();
			
			System.out.println("�ͻ����ѷ��ͣ�");
			
			//���շ������Ϣ
			InputStream in = socket.getInputStream();
			BufferedReader br=new BufferedReader(new InputStreamReader(in));
			String str=br.readLine();
			if(str.equals("yes")){
				System.out.println("���ݴ�������");
			}else{
				//�������ݶ�ʧ�����б���
				BackUpImpl bui=new BackUpImpl();
				bui.store(backclient_path, c, true);
				//���·���
				ArrayList clientList =(ArrayList) bui.load(backclient_path, true);
				send(clientList);
			}
			
		} catch (UnknownHostException e) {
			//��־���
			new LoggerImpl().error(e.getMessage());
		} catch (IOException e) {
			//�����쳣��������
			BackUpImpl bui=new BackUpImpl();
			bui.store(backclient_path, c, true);
			//���·���
			ArrayList clientList =(ArrayList) bui.load(backclient_path, true);
			send(clientList);
			
			new LoggerImpl().error(e.getMessage());
		}
	}
	
	@Test
	public void test1() throws Exception{
		try {
			ArrayList<BIDR> list=(ArrayList<BIDR>) new GatherImpl().gather();
			ArrayList<Integer> list1=new ArrayList<Integer>();
			ArrayList list2=new ArrayList();
			list1.add(list.size());//�����ж������Ƿ�ʧ
			list2.add(list);
			list2.add(list1);
			new ClientImpl().send(list2);
			
			//���̲߳���
//			new ClientImpl().send(list1);
		} catch (Exception e) {
			new LoggerImpl().error(e.getMessage());
		}
	}
}
