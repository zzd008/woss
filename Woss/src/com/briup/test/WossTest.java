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
			list1.add(list.size());//用于判断数据是否丢失
			list2.add(list);
			list2.add(list1);
			client.send(list2);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*System.out.println("woss项目开始--------------");
		System.out.println("客户端正在采集数据--------------");
		System.out.println("数据采集成功，共14895条！");
		System.out.println("服务端开启，准备接受数据--------------");
		System.out.println("客户端正在发送采集的数据--------------");
		System.out.println("服务端接受数据成功，数据无丢失，共14895条！");
		System.out.println("客户端已成功发送且服务端正确接收！");
		System.out.println("数据开始入库--------------");
		System.out.println("数据入库成功，未发生异常！--------------");
		System.out.println("woss项目成功结束！");*/
		
	}
}
