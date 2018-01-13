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
	//key是二级标签的名字，value是二级标签中所有类的父接口
	private Map<String,WossModule> map = new HashMap<String,WossModule>();
	
	
	public ConfigurationImpl(){
		this("src/woss.xml");
	}
	
	private ConfigurationImpl(String xmlPath){
		//构建解析器
		SAXReader dom=new SAXReader();
		//读取xml文件
		try {
			Document doc= dom.read(xmlPath);
			Element root = doc.getRootElement();//获取根标签
			List<Element> list = root.elements();
			//处理二级标签
			for(Element e:list){
				String key=e.getName();//标签名
				String className=e.attributeValue("class");//包名+类名
				//反射创建对象 object.init() error 所以用WossModule
				WossModule woss=(WossModule)Class.forName(className).newInstance();
				Properties p=new Properties();
				
				//处理三级标签
				List<Element> list1 = e.elements();
				for(Element e1:list1){
					p.setProperty(e1.getName(), e1.getText());
				}
				//初始化对象
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
