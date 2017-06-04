package com.itcast.mobilesafe09.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.itcast.mobilesafe09.bean.AppInfo;

/**
 * 提供应用信息的工具类
 * 
 * app安装流程:
 * 
 * 1. 第三方app: 将apk文件拷贝到data/app目录下, 安装完成!
 * 2. 系统app: system/app目录下, 如果要卸载系统内置软件, 进入该目录删除apk,重启手机即可
 * 3. 必须有ROOT权限(管理员权限),才可以修改系统目录. 手机出厂之后,没有管理员权限; 一键Root大师
 */
public class AppInfoProvider {

	//获取已安装的软件列表
	public static ArrayList<AppInfo> getInstalledAppList(Context ctx) {
		//包管理器
		PackageManager pm = ctx.getPackageManager();
		List<PackageInfo> installedPackages = pm.getInstalledPackages(0);

		ArrayList<AppInfo> list = new ArrayList<AppInfo>();
		for (PackageInfo packageInfo : installedPackages) {
			AppInfo info = new AppInfo();

			String packageName = packageInfo.packageName;//包名

			//应用信息
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			String name = applicationInfo.loadLabel(pm).toString();//应用名称
			Drawable icon = applicationInfo.loadIcon(pm);//应用图标

			String sourceDir = applicationInfo.sourceDir;//安装包路径
			long size = new File(sourceDir).length();
			
			//uid: 当手机安装完一款app之后,会给这个app分配一个uid来标示
			int uid = applicationInfo.uid;

			info.packageName = packageName;
			info.name = name + uid;
			info.icon = icon;
			info.size = size;
			//System.out.println("应用名称:" + name + ";安装路径:" + sourceDir);

			//判断apk安装位置, 手机内存&sdcard
			//状态机
			int flags = applicationInfo.flags;//当前应用的标记

			if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
				//具有系统app的特性
				info.isUserApp = false;
			} else {
				//用户应用
				info.isUserApp = true;
			}

			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
				//安装在sdcard
				info.isRom = false;
			} else {
				//安装在手机内存
				info.isRom = true;
			}

			list.add(info);
		}

		return list;
	}

}
