package com.itcast.mobilesafe09.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.itcast.mobilesafe09.engine.ProcessInfoProvider;

/**
 * 锁屏自动清理
 */
public class AutoKillService extends Service {

	private AutoKillReceiver mReceiver;
	private Timer mTimer;

	@Override
	public void onCreate() {
		super.onCreate();

		mReceiver = new AutoKillReceiver();

		//监听屏幕关闭广播
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);

		//定时器
		mTimer = new Timer();

		//以固定频率间隔一段时间执行一次
		//参1: 要执行的任务; 参2:第一次执行时延时时间; 参3:间隔时间
		mTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				System.out.println("定时清理啦!!!");
			}
		}, 0, 5000);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		unregisterReceiver(mReceiver);
		mReceiver = null;

		//停止定时器
		mTimer.cancel();
		mTimer = null;
	}

	class AutoKillReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("屏幕关闭了");
			ProcessInfoProvider.killAllProcesses(context);
		}

	}

}
