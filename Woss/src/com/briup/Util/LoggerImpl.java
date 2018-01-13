package com.briup.Util;

import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import com.briup.util.Logger;
import com.briup.woss.WossModule;

public class LoggerImpl implements Logger,WossModule{
	
	private String log4j_path;

	private org.apache.log4j.Logger log=org.apache.log4j.Logger.getRootLogger();//获取根log对象
	private org.apache.log4j.Logger log1=org.apache.log4j.Logger.getLogger(LoggerImpl.class);
	

	public void init(Properties arg0) {
		log4j_path=arg0.getProperty("log4j_path");
		PropertyConfigurator.configure(log4j_path);
	}

	public void debug(String arg0) {
		//小于error级别的不会进行输出处理
		log.debug(arg0);
		//获取log4j文件中debug的日志处理对象
//		log1.getLogger("myLog1").debug(arg0);
	}

	public void error(String arg0) {
		log.error(arg0);//控制台打印，并输出到文件log_error.txt
	}

	public void fatal(String arg0) {
		log.fatal(arg0);
	}

	public void info(String arg0) {
		log.info(arg0);
//		log1.getLogger("log").info(arg0);;
	}

	public void warn(String arg0) {
		log1.getLogger("mylog").warn(arg0);//控制台打印，并输出到文件woss_warn.txt
	}

	public static void main(String[] args) {
//		new LoggerImpl().debug("aaa");
//		new LoggerImpl().error("aaa");
		new LoggerImpl().info("aaa");
		
		try {
			int i=1/0;
		} catch (Exception e) {
			new LoggerImpl().error(e.getMessage());
		}
	}
}
