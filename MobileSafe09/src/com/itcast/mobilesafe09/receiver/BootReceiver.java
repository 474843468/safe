package com.itcast.mobilesafe09.receiver;

import com.itcast.mobilesafe09.global.GlobalConstants;
import com.itcast.mobilesafe09.utils.PrefUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 监听开机重启广播
 */
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//判断防盗保护总开关是否开启
		boolean protect = PrefUtils.getBoolean(context,
				GlobalConstants.PREF_PROTECT, false);
		if (!protect) {
			return;
		}

		//检测sim卡变化
		String bindSim = PrefUtils.getString(context, GlobalConstants.PREF_SIM,
				null);
		if (TextUtils.isEmpty(bindSim)) {
			//未绑定
			return;
		}

		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String currentSim = tm.getSimSerialNumber() + "!";//当前sim卡, 加"!"故意让两次sim卡不一致, 测试手机危险的情况

		if (!bindSim.equals(currentSim)) {
			System.out.println("手机危险, 发送报警短信!!!");
			//给安全号码发送报警短信
			String phone = PrefUtils.getString(context,
					GlobalConstants.PREF_SAFE_PHONE, "");

			//短信管理器
			SmsManager sm = SmsManager.getDefault();
			//发送短信, android.permission.SEND_SMS
			sm.sendTextMessage(phone, null, "sim card changed!!!!", null, null);
		} else {
			System.out.println("手机安全!");
		}

	}

}
