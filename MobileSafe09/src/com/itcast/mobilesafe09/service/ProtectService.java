package com.itcast.mobilesafe09.service;

import com.itcast.mobilesafe09.R;
import com.itcast.mobilesafe09.activity.SplashActivity;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

/**
 * 产生通知栏常驻服务
 * 
 * 1. 了解如何创建通知栏
 * 
 * 2. PendingIntent和Intent区别(面试题)
 * 
 * 3. 前台通知栏保护服务
 */
public class ProtectService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();

		//产生通知栏
		Notification notification = new Notification();

		notification.icon = R.drawable.ic_launcher;//通知栏图标
		notification.tickerText = "黑马卫士, 保卫您的安全!!!";//通知栏提示语, 一闪而过

		//远程布局对象
		RemoteViews view = new RemoteViews(getPackageName(),
				R.layout.layout_notify);
		notification.contentView = view;

		//通过普通intent制定将来跳转的页面
		Intent intent = new Intent();
		intent.setClass(this, SplashActivity.class);

		//延迟的意图(是对普通intent的包装)
		//当意图激活时,启动一个activity(也可以启动服务,发送广播)
		//参1: context; 参2:请求码,可以随便传;参3:待激活的intent;参4:标记,一般不变
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		//PendingIntent和Intent区别
		//PendingIntent: 不知道何时才会启动的意图, 但是一旦启动,就可以执行相关的操作; 锦囊妙计

		//通知栏点击后跳转页面
		notification.contentIntent = pendingIntent;

		//在前台显示通知栏.
		//参1: 通知栏id, 大于0的整数
		startForeground(1000, notification);
		
		//1. 回收空进程
		//2. 回收后台activity
		//3. 回收服务
		//4. 回收前台页面(极少走到此处)
		
		//由于服务创建了通知栏, 而通知栏是前台页面,不被回收, 那么通知栏反过来也保护了服务, 让服务不容易被回收
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
