package com.briup.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.briup.util.BackUP;
import com.briup.util.Configuration;
import com.briup.util.Logger;
import com.briup.woss.ConfigurationAWare;
import com.briup.woss.WossModule;
import com.briup.woss.client.Client;
import com.briup.woss.client.Gather;
import com.briup.woss.server.DBStore;
import com.briup.woss.server.Server;

public class ConfigurationImpl implements Configuration{
	//key�Ƕ�����ǩ�����֣�value�Ƕ�����ǩ��������ĸ��ӿ�
	private Map<String,WossModule> map = new HashMap<String,WossModule>();
	
	
	public ConfigurationImpl(){
		this("src/woss.xml");
	}
	
	private ConfigurationImpl(String xmlPath){
		//����������
		SAXReader dom=new SAXReader();
		//��ȡxml�ļ�
		try {
			Document doc= dom.read(xmlPath);
			Element root = doc.getRootElement();//��ȡ����ǩ
			List<Element> list = root.elements();
			//���������ǩ
			for(Element e:list){
				String key=e.getName();//��ǩ��
				String className=e.attributeValue("class");//����+����
				//���䴴������ object.init() error ������WossModule
				WossModule woss=(WossModule)Class.forName(className).newInstance();
				Properties p=new Properties();
				
				//����������ǩ
				List<Element> list1 = e.elements();
				for(Element e1:list1){
					p.setProperty(e1.getName(), e1.getText());
				}
				//��ʼ������
				woss.init(p);
				if(woss instanceof ConfigurationAWare){
					((ConfigurationAWare)woss).setConfiguration(this);
				}
				map.put(key, woss);
			}
			
			
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e2) {
			e2.printStackTrace();
		} catch (IllegalAccessException e2) {
			e2.printStackTrace();
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		}
	}

	public BackUP getBackup() throws Exception {
		return (BackUP) map.get("backup");
	}

	public Client getClient() throws Exception {
		return (Client) map.get("client");
	}

	public DBStore getDBStore() throws Exception {
		return (DBStore) map.get("dbstore");
	}

	public Gather getGather() throws Exception {
		return (Gather) map.get("gather");
	}

	public Logger getLogger() throws Exception {
		return (Logger) map.get("logger");
	}

	public Server getServer() throws Exception {
		return (Server) map.get("server");
	}

}
