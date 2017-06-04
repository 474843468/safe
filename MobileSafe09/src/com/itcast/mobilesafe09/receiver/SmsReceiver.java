package com.itcast.mobilesafe09.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.itcast.mobilesafe09.R;
import com.itcast.mobilesafe09.service.LocationService;

/**
 * 短信拦截广播
 * android.permission.RECEIVE_SMS
 * 
 *  <receiver android:name="com.itcast.mobilesafe09.receiver.SmsReceiver" >
            <intent-filter android:priority="2147483647" >
                <action android:name="android.provider.Telephony.SMS_RECEIVED" />
            </intent-filter>
    </receiver>
 */
public class SmsReceiver extends BroadcastReceiver {

	private DevicePolicyManager mDPM;
	private ComponentName mComponentName;

	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] objs = (Object[]) intent.getExtras().get("pdus");

		mDPM = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		mComponentName = new ComponentName(context, AdminReceiver.class);

		for (Object object : objs) {//短信超过140个字节,会分为多条短信发送, for循环只循环一次
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
			String originatingAddress = sms.getOriginatingAddress();//来短信号码
			String messageBody = sms.getMessageBody();//短信内容

			System.out.println("来电号码:" + originatingAddress + ";短信内容:"
					+ messageBody);

			if ("#*location*#".equals(messageBody)) {
				//gps追踪
				//启动手机定位服务
				context.startService(new Intent(context, LocationService.class));

				abortBroadcast();//中断短信传递, 4.3+系统会出问题, 无法拦截, 只允许默认短信应用能够拦截; 解决办法: 删除短信数据库记录
			} else if ("#*alarm*#".equals(messageBody)) {
				//播放报警音乐
				//初始化媒体播放器
				MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
				player.setVolume(1, 1);//音量最大
				player.setLooping(true);//单曲循环
				player.start();//开始播放

				abortBroadcast();//中断短信传递
			} else if ("#*wipedata*#".equals(messageBody)) {
				//擦除数据
				if (mDPM.isAdminActive(mComponentName)) {
					mDPM.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
				}

				abortBroadcast();//中断短信传递
			} else if ("#*lockscreen*#".equals(messageBody)) {
				//远程锁屏
				if (mDPM.isAdminActive(mComponentName)) {
					mDPM.lockNow();
					mDPM.resetPassword("123456", 0);
				}
				abortBroadcast();//中断短信传递
			}
		}
	}
}
