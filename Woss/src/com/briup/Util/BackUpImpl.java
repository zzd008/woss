package com.briup.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Properties;

import com.briup.util.BackUP;
import com.briup.woss.WossModule;

public class BackUpImpl implements BackUP,WossModule{
	
	private String back_path;

	public void init(Properties arg0) {
		back_path=arg0.getProperty("back_path");
	}

	//arg1:读完是否删除
	public Object load(String fileName, boolean arg1) throws Exception {
		Object obj=null;
		File f=new File(back_path+fileName);
		if(f.exists()&&f.canRead()){
			ObjectInputStream ois=new ObjectInputStream(new FileInputStream(f));
			obj=ois.readObject();
		}
		if(arg1=true){
			f.delete();
		}
		return obj;
	}

	//arg2：覆盖还是追加
	public void store(String fileName, Object obj, boolean arg2) throws Exception {
		File f=new File(back_path+fileName);
		ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(f,arg2));
		oos.writeObject(obj);
		oos.flush();		
	}
	
}
