package com.itcast.mobilesafe09.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.itcast.mobilesafe09.R;
import com.itcast.mobilesafe09.bean.ProcessInfo;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;

/**
 * 进程相关信息提供者
 */
public class ProcessInfoProvider {

	//获取正在运行的进程列表(了解)
	public static ArrayList<ProcessInfo> getProcessList(Context ctx) {
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningAppProcessInfo> runningAppProcesses = am
				.getRunningAppProcesses();

		PackageManager pm = ctx.getPackageManager();

		ArrayList<ProcessInfo> list = new ArrayList<ProcessInfo>();

		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {

			ProcessInfo info = new ProcessInfo();

			String packageName = runningAppProcessInfo.processName;//获取包名

			info.packageName = packageName;

			//获取某组进程的内存信息
			//pid:进程id
			android.os.Debug.MemoryInfo[] memoryInfos = am
					.getProcessMemoryInfo(new int[] { runningAppProcessInfo.pid });
			android.os.Debug.MemoryInfo memoryInfo = memoryInfos[0];//当前进程内存信息

			int memory = memoryInfo.getTotalPrivateDirty();//当前app占用内存大小, 单位是kb
			info.memory = memory * 1024;

			//通过包名获取当前app的信息
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(
						packageName, 0);

				String name = applicationInfo.loadLabel(pm).toString();//名称
				Drawable icon = applicationInfo.loadIcon(pm);//图标

				info.name = name;
				info.icon = icon;

				//状态机
				int flags = applicationInfo.flags;//当前应用的标记

				if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
					//系统进程
					info.isUserProcess = false;
				} else {
					//用户进程
					info.isUserProcess = true;
				}

			} catch (NameNotFoundException e) {
				//没有找到该包名信息的异常
				//某些系统应用, 没有名称和图标, 会走到此异常中
				//默认名称和图标
				info.icon = ctx.getResources().getDrawable(
						R.drawable.process_default);
				info.name = info.packageName;//以包名作为名称
				info.isUserProcess = false;

				e.printStackTrace();
			}

			list.add(info);
		}

		return list;
	}

	//获取正在运行的进程数量
	public static int getRunningProcessNum(Context ctx) {
		//活动管理器, 系统中一切正在运行的东西(内存),都可以通过活动管理器来管理
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);

		return am.getRunningAppProcesses().size();
	}

	//获取可有进程数量
	public static int getTotalProcessNum(Context ctx) {
		//android中每个app, 至少占用一个进程
		// <service
		//        android:name="com.itcast.mobilesafe09.service.BlackNumberService"
		//        android:process="com.itcast.mobilesafe09.blacknumber" >
		// </service>
		//四大组件,都可以单独占用一个进程, service更常用
		PackageManager pm = ctx.getPackageManager();
		//除了获取基本信息之外,还要获取四大组件信息
		List<PackageInfo> installedPackages = pm
				.getInstalledPackages(PackageManager.GET_ACTIVITIES
						| PackageManager.GET_PROVIDERS
						| PackageManager.GET_RECEIVERS
						| PackageManager.GET_SERVICES);

		//ArrayList<String> list = new ArrayList<String>();
		//注意: 1. 应用程序不一样, 但进程名一样, 也要计算数量
		// 2. 有可能出现没有四大组件的app

		int count = 0;

		for (PackageInfo packageInfo : installedPackages) {
			HashSet<String> set = new HashSet<String>();//不允许有重复元素

			//当前app默认进程, 默认进程名 = 包名
			set.add(packageInfo.applicationInfo.processName);

			//activity组件的进程
			ActivityInfo[] activities = packageInfo.activities;

			if (activities != null) {
				for (ActivityInfo activityInfo : activities) {
					String processName = activityInfo.processName;//当前activity所在的进程
					set.add(processName);
				}
			}

			//service组件的进程
			ServiceInfo[] services = packageInfo.services;

			if (services != null) {
				for (ServiceInfo serviceInfo : services) {
					set.add(serviceInfo.processName);
				}
			}

			//receiver组件的进程
			ActivityInfo[] receivers = packageInfo.receivers;
			if (receivers != null) {
				for (ActivityInfo activityInfo : receivers) {
					set.add(activityInfo.processName);
				}
			}

			//provider组件的进程
			ProviderInfo[] providers = packageInfo.providers;
			if (providers != null) {
				for (ProviderInfo providerInfo : providers) {
					set.add(providerInfo.processName);
				}
			}

			count += set.size();
		}

		return count;
	}

	//获取可用内存
	public static long getAvailMemory(Context ctx) {
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);

		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);//获取内存信息, 会给空的MemoryInfo字段赋值

		return outInfo.availMem;
	}

	//获取总内存大小
	public static long getTotalMemory(Context ctx) {
		//		ActivityManager am = (ActivityManager) ctx
		//				.getSystemService(Context.ACTIVITY_SERVICE);
		//
		//		MemoryInfo outInfo = new MemoryInfo();
		//		am.getMemoryInfo(outInfo);//获取内存信息, 会给空的MemoryInfo字段赋值

		//outInfo.totalMem; api16+才可以用
		//解决办法: 读系统配置文件 /proc/meminfo

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("/proc/meminfo"));

			//MemTotal:         510704 kB
			String readLine = reader.readLine();//读取第一行内容

			char[] charArray = readLine.toCharArray();//转换为字符数组, 然后一个一个判断是否是数字

			StringBuffer sb = new StringBuffer();

			for (char c : charArray) {
				if (c >= '0' && c <= '9') {
					//发现数字
					sb.append(c);
				}
			}

			String number = sb.toString();//510704

			return Long.parseLong(number) * 1024;//转成字节返回

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return 0;
	}

	//杀死所有进程
	public static void killAllProcesses(Context ctx) {
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);

		ArrayList<ProcessInfo> processList = getProcessList(ctx);
		for (ProcessInfo info : processList) {
			//跳过手机卫士
			if (info.packageName.equals(ctx.getPackageName())) {
				continue;
			}

			am.killBackgroundProcesses(info.packageName);
		}
	}
}
