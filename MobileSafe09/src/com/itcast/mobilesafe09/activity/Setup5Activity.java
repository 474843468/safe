package com.itcast.mobilesafe09.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;

import com.itcast.mobilesafe09.R;
import com.itcast.mobilesafe09.global.GlobalConstants;
import com.itcast.mobilesafe09.utils.PrefUtils;

/**
 * 手机防盗-设置向导5
 */
public class Setup5Activity extends BaseSetupActivity {

	private CheckBox cbProtect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup5);

		cbProtect = (CheckBox) findViewById(R.id.cb_protect);

		//回显
		boolean protect = PrefUtils.getBoolean(this,
				GlobalConstants.PREF_PROTECT, false);
		cbProtect.setChecked(protect);//设置勾选框选中状态
	}

	@Override
	public void go2Next() {
		//保存防盗保护总开关的状态
		PrefUtils.putBoolean(this, GlobalConstants.PREF_PROTECT,
				cbProtect.isChecked());

		//用户已经配置完成, 记录在sp中, 下次进入,不在配置,直接主页面
		PrefUtils.putBoolean(this, GlobalConstants.PREF_CONFIG, true);

		//进入手机防盗主页面
		startActivity(new Intent(this, LostFindActivity.class));
		finish();

		//切换activity动画
		//参1: 进入动画; 参2:退出动画
		overridePendingTransition(R.anim.anim_next_enter, R.anim.anim_next_exit);
	}

	@Override
	public void go2Pre() {
		startActivity(new Intent(this, Setup4Activity.class));
		finish();
		//切换activity动画
		//参1: 进入动画; 参2:退出动画
		overridePendingTransition(R.anim.anim_pre_enter, R.anim.anim_pre_exit);
	}

}
