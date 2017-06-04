package com.itcast.mobilesafe09.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.itcast.mobilesafe09.db.dao.AddressDao;
import com.itcast.mobilesafe09.view.AddressToast;

/**
 * 归属地显示服务
 * 
 * 开发流程:
 * 1. 监听来电, TelephonyManager
 * 2. 监听去电, 动态注册广播
 * 3. 自定义Toast, WindowManager
 * 
 */
public class AddressService extends Service {

	private TelephonyManager mTM;
	private MyPhoneStateListener mListener;
	private OutCallReceiver mReceiver;
	//private WindowManager mWM;
	//private TextView mView;

	private AddressToast mToast;

	@Override
	public void onCreate() {
		super.onCreate();

		//监听来电
		mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

		//来电监听器
		mListener = new MyPhoneStateListener();

		//开始监听
		//参1:监听器;参2:表示监听电话状态
		mTM.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);

		//监听去电, 接收广播
		//动态注册广播
		//android.permission.PROCESS_OUTGOING_CALLS
		mReceiver = new OutCallReceiver();

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(mReceiver, filter);

		mToast = new AddressToast(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//停止电话状态监听
		mTM.listen(mListener, PhoneStateListener.LISTEN_NONE);
		mListener = null;

		//注销广播
		unregisterReceiver(mReceiver);
		mReceiver = null;
	}

	//来电监听器
	class MyPhoneStateListener extends PhoneStateListener {

		//电话状态发生变化
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				//电话铃声响了
				//incomingNumber:来电电话
				String address = AddressDao.getAddress(getApplicationContext(),
						incomingNumber);
				//ToastUtils.showToast(getApplicationContext(), address);
				//showToast(address);
				mToast.show(address);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				//摘机状态, 接听电话

				break;
			case TelephonyManager.CALL_STATE_IDLE:
				//空闲状态
				System.out.println("电话挂断了");

				//移除窗口布局
				//				if (mWM != null && mView != null) {
				//					mWM.removeView(mView);
				//				}
				mToast.hide();
				break;

			default:
				break;
			}

		}
	}

	//去电监听的广播
	class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			//获取去电号码
			String number = getResultData();
			String address = AddressDao.getAddress(getApplicationContext(),
					number);
			//ToastUtils.showToast(getApplicationContext(), address);
			//showToast(address);
			mToast.show(address);
		}

	}

	//显示自定义吐司
	//	private void showToast(String text) {
	//		//窗口管理器
	//		//窗口: Android系统中最高级别的布局, 所有的布局都显示在一个窗口中, activity, dialog,状态栏, 桌面
	//		//当没有自己的页面(activtiy), 但需要在屏幕上显示一个布局的时候, 可以直接使用窗口来添加布局
	//		mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
	//
	//		//初始化布局参数(能看懂, 直接抄)
	//		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
	//		params.height = WindowManager.LayoutParams.WRAP_CONTENT;//布局高度
	//		params.width = WindowManager.LayoutParams.WRAP_CONTENT;//布局宽度
	//		//标记, 定义布局的特点, 没有焦点, 不能触摸, 保持屏幕常亮
	//		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
	//				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
	//				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
	//		//显示格式,显示效果,此属性不重要,可以忽略
	//		params.format = PixelFormat.TRANSLUCENT;
	//		//吐司动画
	//		//params.windowAnimations = com.android.internal.R.style.Animation_Toast;
	//		//窗口布局类型, 吐司类型
	//		params.type = WindowManager.LayoutParams.TYPE_TOAST;
	//		//窗口布局标题
	//		//params.setTitle("Toast");
	//
	//		//初始化布局
	//		mView = new TextView(this);
	//		mView.setText(text);
	//		mView.setTextColor(Color.RED);
	//		mView.setTextSize(30);
	//
	//		//给窗口添加布局, 参1:布局对象; 参2:布局参数
	//		mWM.addView(mView, params);
	//	}

}
