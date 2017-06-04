package com.itcast.mobilesafe09.bean;

import android.graphics.drawable.Drawable;

/**
 * 进程信息
 */
public class ProcessInfo {

	public String packageName;
	public String name;
	public Drawable icon;
	public long memory;

	public boolean isUserProcess;//是否是用户进程
	public boolean isChecked;//标记当前条目是否被选中

}
