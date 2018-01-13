package com.briup.util;

import com.briup.woss.WossModule;
/**
 * 该接口提供了日志模块的规范。
 * 日志模块将日志信息划分为五种级别，
 * 具体不同级别的日志的记录的格式、记录方式等内容有具体实现类来决定。
 * 
 * @author briup
 * @version 1.0 2010-9-14
 */
public interface Logger extends WossModule{
	/**
	 * 记录<tt>Debug</tt>级别的日志
	 * @param msg 需要记录的日志信息
	 */
	public void debug(String msg);
	/**
	 * 记录<tt>Debug</tt>级别的日志
	 * @param msg 需要记录的日志信息
	 * @param key 根据提供的不同的key，对日志输出的方式提供更灵活的配置。
	 */
	public void debug(String msg,Object key);
	/**
	 * 记录<tt>Info</tt>级别的日志
	 * @param msg 需要记录的日志信息
	 */
	public void info(String msg);
	/**
	 * 记录<tt>Info</tt>级别的日志
	 * @param msg 需要记录的日志信息
	 * @param key 根据提供的不同的key，对日志输出的方式提供更灵活的配置。
	 */
	public void info(String msg,Object key);
	/**
	 * 记录<tt>Warn</tt>级别的日志
	 * @param msg 需要记录的日志信息
	 */
	public void warn(String msg);
	/**
	 * 记录<tt>Warn</tt>级别的日志
	 * @param msg 需要记录的日志信息
	 * @param key 根据提供的不同的key，对日志输出的方式提供更灵活的配置。
	 */
	public void warn(String msg,Object key);
	/**
	 * 记录<tt>Error</tt>级别的日志
	 * @param msg 需要记录的日志信息
	 */
	public void error(String msg);
	/**
	 * 记录<tt>Error</tt>级别的日志
	 * @param msg 需要记录的日志信息
	 * @param key 根据提供的不同的key，对日志输出的方式提供更灵活的配置。
	 */
	public void error(String msg,Object key);
	/**
	 * 记录<tt>Fatal</tt>级别的日志
	 * @param msg 需要记录的日志信息
	 */
	public void fatal(String msg);
	/**
	 * 记录<tt>Fatal</tt>级别的日志
	 * @param msg 需要记录的日志信息
	 * @param key 根据提供的不同的key，对日志输出的方式提供更灵活的配置。
	 */
	public void fatal(String msg,Object key);
}
