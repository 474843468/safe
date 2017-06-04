package com.itcast.mobilesafe09.global;

/**
 * 维护全局常量
 */
public interface GlobalConstants {

	public static final String PREF_AUTO_UPDATE = "auto_update";//自动更新设置开关
	public static final String PREF_PASSWORD = "password";//手机防盗密码
	public static final String PREF_CONFIG = "config";//记录是否进入过手机防盗设置向导
	public static final String PREF_SIM = "sim";//绑定的sim卡序列号
	public static final String PREF_SAFE_PHONE = "safe_phone";//安全号码
	public static final String PREF_PROTECT = "protect";//防盗保护总开关
	public static final String PREF_ADDRESS_STYLE = "address_style";//归属地风格的位置
	public static final String PREF_SHOW_SYSTEM = "show_system";//是否显示系统进程
	public static final String PREF_SHORTCUT = "shortcut_installed";//是否已经创建过快捷方式
	public static final String ACTION_SKIP_PACKAGE = "com.itcast.mobilesafe09.ACTION_SKIP_PACKAGE";//程序锁广播action
	public static final String CONTENT_URI_APPLOCK = "content://com.itcast.mobilesafe09/applock";//程序锁数据库变化的uri

}
