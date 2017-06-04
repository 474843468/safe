package com.itcast.mobilesafe09.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.itcast.mobilesafe09.R;
import com.itcast.mobilesafe09.receiver.AdminReceiver;
import com.itcast.mobilesafe09.utils.ToastUtils;

/**
 * 手机防盗-设置向导4
 */
public class Setup4Activity extends BaseSetupActivity {

	private RelativeLayout rlAdmin;
	private DevicePolicyManager mDPM;
	private ComponentName mComponentName;
	private ImageView ivAdmin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		rlAdmin = (RelativeLayout) findViewById(R.id.rl_admin);
		ivAdmin = (ImageView) findViewById(R.id.iv_admin);

		mDPM = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
		mComponentName = new ComponentName(this, AdminReceiver.class);

		rlAdmin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//如果没有激活,就去激活页面激活; 否则取消激活
				if (mDPM.isAdminActive(mComponentName)) {
					//取消激活设备管理员
					mDPM.removeActiveAdmin(mComponentName);

					//更新图片
					ivAdmin.setImageResource(R.drawable.admin_inactivated);
				} else {
					//跳到激活页面
					Intent intent = new Intent(
							DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
					intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
							mComponentName);
					intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
							"手机卫士,保护您的安全!!!!");
					startActivity(intent);
				}

			}
		});
	}

	//两个作用:1. 页面创建时,onCreate->onStart, 用于数据回显
	//2. 从激活页面回来时, 用于界面更新
	@Override
	protected void onStart() {
		super.onStart();
		//当从激活页面回来时, 会走onStart方法, 在此处重新获取激活状态来更新界面
		if (mDPM.isAdminActive(mComponentName)) {
			ivAdmin.setImageResource(R.drawable.admin_activated);
		} else {
			ivAdmin.setImageResource(R.drawable.admin_inactivated);
		}
	}

	@Override
	public void go2Next() {
		//必须激活之后才能进入下一个页面
		if (!mDPM.isAdminActive(mComponentName)) {
			ToastUtils.showToast(this, "必须激活设备管理员!");
			return;
		}

		startActivity(new Intent(this, Setup5Activity.class));
		finish();

		//切换activity动画
		//参1: 进入动画; 参2:退出动画
		overridePendingTransition(R.anim.anim_next_enter, R.anim.anim_next_exit);
	}

	@Override
	public void go2Pre() {
		startActivity(new Intent(this, Setup3Activity.class));
		finish();
		//切换activity动画
		//参1: 进入动画; 参2:退出动画
		overridePendingTransition(R.anim.anim_pre_enter, R.anim.anim_pre_exit);
	}

}
