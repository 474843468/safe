package com.itcast.mobilesafe09.service;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.itcast.mobilesafe09.db.dao.BlackNumberDao;

/**
 * 黑名单拦截服务
 * 
 * 开发流程:
 * 1. 设置页面增加开关, 逻辑类似归属地显示的开关
 * 2. 拦截短信, 注册广播, 判断是否需要拦截
 * 3. 拦截电话: 监听来电, 如果是黑名单, 自动挂断
 * 4. 挂断电话: 反射, aidl(了解)
 * 5. 删除通话记录, ContentObserver监听数据变化, 删除记录
 */
public class BlackNumberService extends Service {

	private InnerSmsReceiver mReceiver;
	private BlackNumberDao mDao;

	private TelephonyManager mTM;
	private BlackNumberListener mListener;

	@Override
	public void onCreate() {
		super.onCreate();

		//拦截短信
		mReceiver = new InnerSmsReceiver();

		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(mReceiver, filter);

		mDao = BlackNumberDao.getInstance(this);

		//监听来电
		mListener = new BlackNumberListener();
		mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		mTM.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//注销广播
		unregisterReceiver(mReceiver);
		mReceiver = null;

		//取消监听
		mTM.listen(mListener, PhoneStateListener.LISTEN_NONE);
		mListener = null;
	}

	class BlackNumberListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				//来电
				//判断是否需要拦截
				boolean find = mDao.find(incomingNumber);

				if (find) {
					//拦截电话1, 拦截全部 3
					int mode = mDao.findMode(incomingNumber);

					if (mode != 2) {
						//挂断电话
						endCall();

						//删除通话记录
						//此处不能直接删除通话记录
						//系统添加通话记录比较耗时, 系统还没有添加, 这里就开始删除了, 从而导致不能删除最新的记录
						//deleteCalllog(incomingNumber);
						//解决办法: 系统添加完成之后,再删除->监听系统数据库变化
						//注册内容观察者,观察数据库变化
						//参1: 要观察的数据库的uri
						//参2: 是否要同时通知子uri, content://call_log/calls/id
						//参3: 内容观察者监听
						getContentResolver().registerContentObserver(
								Uri.parse("content://call_log/calls"),
								true,
								new BlackNumberObserver(new Handler(),
										incomingNumber));
					}
				}

				break;

			default:
				break;
			}

		}
	}

	//当权限一样时, 动态注册的广播比静态注册的更先获取广播
	class InnerSmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			Object[] objs = (Object[]) intent.getExtras().get("pdus");

			for (Object object : objs) {//短信超过140个字节,会分为多条短信发送, for循环只循环一次
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
				String originatingAddress = sms.getOriginatingAddress();//来短信号码
				String messageBody = sms.getMessageBody();//短信内容

				System.out.println("黑名单来电号码:" + originatingAddress + ";短信内容:"
						+ messageBody);

				//根据短信内容拦截
				//laogong ,nikawodetoufapiaobupiaoliangya?
				//分词: laogong, nikan,wode ,toufa,piaobupiaoliang
				//分词框架: Lucene
				if (messageBody.contains("fapiao")) {
					abortBroadcast();
				}

				//拦截短信
				boolean exist = mDao.find(originatingAddress);

				if (exist) {
					//拦截短信2, 拦截全部 3
					int mode = mDao.findMode(originatingAddress);

					if (mode > 1) {
						abortBroadcast();
					}
				}

			}

		}

	}

	//挂断电话
	//<uses-permission android:name="android.permission.CALL_PHONE"/>
	//注意拷贝aidl文件
	public void endCall() {
		System.out.println("挂断电话....");
		try {
			//反射获得系统服务的getService方法对象
			Method method = Class.forName("android.os.ServiceManager")
					.getMethod("getService", String.class);

			//执行这个方法得到一个IBinder对象
			IBinder binder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);

			//转换为具体的服务类(ITelephony)接口对象
			ITelephony telephony = ITelephony.Stub.asInterface(binder);

			//结束通话
			telephony.endCall();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	//删除通话记录
	//  <uses-permission android:name="android.permission.READ_CALL_LOG" />
	// <uses-permission android:name="android.permission.WRITE_CALL_LOG" />
	public void deleteCalllog(String incomingNumber) {
		//删除系统通话记录
		//联系人数据库->calls表
		getContentResolver().delete(Uri.parse("content://call_log/calls"),
				"number=?", new String[] { incomingNumber });

	}

	//内容观察者
	class BlackNumberObserver extends ContentObserver {

		private String incomingNumber;

		public BlackNumberObserver(Handler handler, String incomingNumber) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}

		//数据库发生变化了
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			System.out.println("数据库发生变化了");

			//删除通话记录
			deleteCalllog(incomingNumber);

			//注销内容观察者
			getContentResolver().unregisterContentObserver(this);
		}

	}
}
