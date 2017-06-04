package com.itcast.mobilesafe09.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;

/**
 * 判断服务运行状态
 */
public class ServiceStatusUtils {

	public static boolean isServiceRunning(Context ctx,
			Class<? extends Service> clazz) {
		//1. 获取所有正在运行的服务
		//2. 遍历服务, 和要判断的服务进行比对
		//3. 如果要判断的服务和正在运行的服务匹配, 说明正在运行
		//活动管理器, 系统中一切正在运行的东西(内存),都可以通过活动管理器来管理
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningServices = am.getRunningServices(100);//参数表示最多返回100条记录

		for (RunningServiceInfo runningServiceInfo : runningServices) {
			String className = runningServiceInfo.service.getClassName();//获取正在运行服务的类名
			//System.out.println(className);

			if (className.equals(clazz.getName())) {
				return true;//服务存在
			}
		}

		return false;
	}
}
