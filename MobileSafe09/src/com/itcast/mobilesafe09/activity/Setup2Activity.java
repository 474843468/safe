package com.itcast.mobilesafe09.activity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.itcast.mobilesafe09.R;
import com.itcast.mobilesafe09.global.GlobalConstants;
import com.itcast.mobilesafe09.utils.PrefUtils;
import com.itcast.mobilesafe09.utils.ToastUtils;

/**
 * 手机防盗-设置向导2
 */
public class Setup2Activity extends BaseSetupActivity {

	private RelativeLayout rlBind;
	private ImageView ivBind;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);

		rlBind = (RelativeLayout) findViewById(R.id.rl_bind);
		ivBind = (ImageView) findViewById(R.id.iv_bind);

		//数据回显
		String bindSim = PrefUtils.getString(getApplicationContext(),
				GlobalConstants.PREF_SIM, null);
		if (TextUtils.isEmpty(bindSim)) {
			ivBind.setImageResource(R.drawable.unlock);
		} else {
			ivBind.setImageResource(R.drawable.lock);
		}

		rlBind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//如果没绑定, 就绑定; 否则就解绑
				String bindSim = PrefUtils.getString(getApplicationContext(),
						GlobalConstants.PREF_SIM, null);
				if (TextUtils.isEmpty(bindSim)) {
					//没绑定
					//绑定: 将当前sim卡序列号记录下来
					//电话管理器
					//android.permission.READ_PHONE_STATE
					TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					String simSerialNumber = tm.getSimSerialNumber();//sim卡序列号
					System.out.println("序列号:" + simSerialNumber);

					PrefUtils.putString(getApplicationContext(),
							GlobalConstants.PREF_SIM, simSerialNumber);

					//更新ui
					ivBind.setImageResource(R.drawable.lock);
				} else {
					//解绑
					//从sp中删除之前的序列号
					PrefUtils.remove(getApplicationContext(),
							GlobalConstants.PREF_SIM);

					//更新ui
					ivBind.setImageResource(R.drawable.unlock);
				}
			}
		});
	}

	//下一页逻辑
	public void go2Next() {
		//如果没有绑定sim卡, 不允许下一步
		String bindSim = PrefUtils.getString(getApplicationContext(),
				GlobalConstants.PREF_SIM, null);
		if (TextUtils.isEmpty(bindSim)) {
			ToastUtils.showToast(this, "必须绑定sim卡!!!");
			return;
		}

		startActivity(new Intent(this, Setup3Activity.class));
		finish();

		//切换activity动画
		//参1: 进入动画; 参2:退出动画
		overridePendingTransition(R.anim.anim_next_enter, R.anim.anim_next_exit);
	}

	//上一页逻辑
	public void go2Pre() {
		startActivity(new Intent(this, Setup1Activity.class));
		finish();

		//切换activity动画
		//参1: 进入动画; 参2:退出动画
		overridePendingTransition(R.anim.anim_pre_enter, R.anim.anim_pre_exit);
	}
}
