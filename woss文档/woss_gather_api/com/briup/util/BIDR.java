package com.briup.util;

import java.io.Serializable;
import java.sql.Timestamp;
/**
 * BIDR表示一个用户的一次的上网记录。
 * 它包括了用户的登录名、分配的IP、上线时间、下线时间、上线时长、NAS的IP。
 * 
 * @author briup
 * @version 1.0 2010-9-14
 *
 */
public class BIDR implements Serializable{
	private static final long serialVersionUID = -4446311230799073644L;
	/**
	 * 用户的登录名
	 */
	private String AAA_login_name;
	/**
	 * 用户某次上网时所分配的IP地址
	 */
	private String login_ip;
	/**
	 * 用户上网的上线时间
	 */
	private Timestamp login_date;
	/**
	 * 用户上网的下线时间
	 */
	private Timestamp logout_date;
	/**
	 * 用户上网所使用的NAS服务器的IP地址
	 */
	private String NAS_ip;
	/**
	 * 用户本次上网的累计时间
	 */
	private Integer time_deration;
	public BIDR() {}

	public BIDR(String aaa_login_name, String login_ip, Timestamp login_date,
			Timestamp logout_date, String nas_ip, Integer time_deration) {
		AAA_login_name = aaa_login_name;
		this.login_ip = login_ip;
		this.login_date = login_date;
		this.logout_date = logout_date;
		NAS_ip = nas_ip;
		this.time_deration = time_deration;
	}

	public String getAAA_login_name() {
		return AAA_login_name;
	}

	public void setAAA_login_name(String aAA_login_name) {
		AAA_login_name = aAA_login_name;
	}

	public String getLogin_ip() {
		return login_ip;
	}

	public void setLogin_ip(String login_ip) {
		this.login_ip = login_ip;
	}

	public Timestamp getLogin_date() {
		return login_date;
	}

	public void setLogin_date(Timestamp login_date) {
		this.login_date = login_date;
	}

	public Timestamp getLogout_date() {
		return logout_date;
	}

	public void setLogout_date(Timestamp logout_date) {
		this.logout_date = logout_date;
	}

	public String getNAS_ip() {
		return NAS_ip;
	}

	public void setNAS_ip(String nAS_ip) {
		NAS_ip = nAS_ip;
	}

	public Integer getTime_deration() {
		return time_deration;
	}

	public void setTime_deration(Integer time_deration) {
		this.time_deration = time_deration;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
