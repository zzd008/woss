package com.briup.util;

import com.briup.woss.client.Client;
import com.briup.woss.client.Gather;
import com.briup.woss.server.DBStore;
import com.briup.woss.server.Server;

/**
 * Configuration接口提供了配置模块的规范。
 * 配置模块通过某种配置方式将Logger、BackUP、Gather、Client、Server、DBStore等模块的实现类进行实例化，
 * 并且将其所需要配置信息予以传递。通过配置模块可以获得各个模块的实例。
 * 
 * @author briup
 * @version 1.0 2010-9-14
 */
public interface Configuration {
	// public Logger getClientLogger()throws Exception;
	// public Logger getServerLogger()throws Exception;
	/**
	 * 获取<tt>日志</tt>模块的实例
	 */
	public Logger getLogger() throws Exception;
	/**
	 * 获取<tt>备份</tt>模块的实例
	 */
	public BackUP getBackup() throws Exception;
	/**
	 * 获取<tt>采集</tt>模块的实例
	 */
	public Gather getGather() throws Exception;
	/**
	 * 获取<tt>客户端</tt>的实例
	 */
	public Client getClient() throws Exception;
	/**
	 * 获取<tt>服务器端</tt>的实例
	 */
	public Server getServer() throws Exception;
	/**
	 * 获取<tt>入库</tt>模块的实例
	 */
	public DBStore getDBStore() throws Exception;
}
