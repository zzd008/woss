package com.briup.test;

import java.util.ArrayList;

import com.briup.ClientImpl.ClientImpl;
import com.briup.ClientImpl.GatherImpl;
import com.briup.Util.ConfigurationImpl;
import com.briup.util.BIDR;
import com.briup.woss.client.Client;
import com.briup.woss.client.Gather;

public class WossTest {
	public static void main(String[] args) {
		final ConfigurationImpl con=new ConfigurationImpl();
		try {
			new Thread(new Runnable() {
				public void run() {
					try {
						con.getServer().revicer();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
			
			Client client = con.getClient();
			Gather gather = con.getGather();
			ArrayList<BIDR> list=(ArrayList<BIDR>) gather.gather();
			ArrayList<Integer> list1=new ArrayList<Integer>();
			ArrayList list2=new ArrayList();
			list1.add(list.size());//�����ж������Ƿ�ʧ
			list2.add(list);
			list2.add(list1);
			client.send(list2);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*System.out.println("woss��Ŀ��ʼ--------------");
		System.out.println("�ͻ������ڲɼ�����--------------");
		System.out.println("���ݲɼ��ɹ�����14895����");
		System.out.println("����˿�����׼����������--------------");
		System.out.println("�ͻ������ڷ��Ͳɼ�������--------------");
		System.out.println("����˽������ݳɹ��������޶�ʧ����14895����");
		System.out.println("�ͻ����ѳɹ������ҷ������ȷ���գ�");
		System.out.println("���ݿ�ʼ���--------------");
		System.out.println("�������ɹ���δ�����쳣��--------------");
		System.out.println("woss��Ŀ�ɹ�������");*/
		
	}
}
