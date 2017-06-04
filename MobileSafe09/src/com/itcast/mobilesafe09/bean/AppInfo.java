package com.itcast.mobilesafe09.bean;

import android.graphics.drawable.Drawable;

/**
 * 应用信息
 */
public class AppInfo {

	public String packageName;
	public String name;
	public Drawable icon;
	public long size;

	public boolean isUserApp;//标记是否是用户app; true表示用户, false表示系统app
	public boolean isRom;//标记是否安装在手机内存;false表示sdcard

}
