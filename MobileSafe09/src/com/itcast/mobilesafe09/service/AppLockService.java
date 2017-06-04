package com.itcast.mobilesafe09.service;

import java.util.ArrayList;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;

import com.itcast.mobilesafe09.activity.EnterPwdActivity;
import com.itcast.mobilesafe09.db.dao.AppLockDao;
import com.itcast.mobilesafe09.global.GlobalConstants;

/**
 * 程序锁辅助功能服务
 * 
 * 配置流程:
 * 1. 写一个服务,继承AccessibilityService(非重点)
 * 2. 在清单文件中按照文档对服务进行声明
 * 3. xml配置文件
 * 4. 运行手机卫士, 在设置->辅助功能->手机卫士09程序锁 中开启和关闭服务
 * 
 */
public class AppLockService extends AccessibilityService {

	private AppLockDao mDao;
	private AppLockReceiver mReceiver;
	private String mSkipPackage;//要跳过验证的包名

	private ArrayList<String> mList;//所有已加锁app

	private AppLockObserver mObserver;

	@Override
	public void onCreate() {
		super.onCreate();
		mDao = AppLockDao.getInstance(this);

		//加载所有已加锁的app
		mList = mDao.findAll();

		//注册广播
		mReceiver = new AppLockReceiver();
		IntentFilter filter = new IntentFilter(
				GlobalConstants.ACTION_SKIP_PACKAGE);
		registerReceiver(mReceiver, filter);

		//注册程序锁数据库变化观察者
		mObserver = new AppLockObserver(new Handler());
		getContentResolver()
				.registerContentObserver(
						Uri.parse(GlobalConstants.CONTENT_URI_APPLOCK), true,
						mObserver);
	}

	//事件发生后的回调
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {

		if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
			//窗口状态发生变化了
			String packageName = event.getPackageName().toString();//当前窗口包名
			System.out.println("packageName:" + packageName);

			//判断当前包是否加锁, 如果加锁,就跳到输入密码页面
			//if (mDao.find(packageName)) {
			if (mList.contains(packageName)) {//减少数据库查询频率, 提高性能

				//判断是否需要跳过验证
				if (!packageName.equals(mSkipPackage)) {
					Intent intent = new Intent(this, EnterPwdActivity.class);
					intent.putExtra("packageName", packageName);//要加锁的包名
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}

			}
		}

	}

	@Override
	public void onInterrupt() {

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
		mReceiver = null;

		getContentResolver().unregisterContentObserver(mObserver);
		mObserver = null;
	}

	class AppLockReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			mSkipPackage = intent.getStringExtra("packageName");
		}

	}

	//程序锁数据库变化的观察者
	class AppLockObserver extends ContentObserver {

		public AppLockObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			//重新加载最新数据
			mList = mDao.findAll();
		}
	}

}
