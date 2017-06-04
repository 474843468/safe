package com.itcast.mobilesafe09.activity;

import android.content.Intent;
import android.os.Bundle;

import com.itcast.mobilesafe09.R;

/**
 * 手机防盗-设置向导1
 */
public class Setup1Activity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}

	@Override
	public void go2Next() {
		startActivity(new Intent(this, Setup2Activity.class));
		finish();

		//切换activity动画
		//参1: 进入动画; 参2:退出动画
		overridePendingTransition(R.anim.anim_next_enter, R.anim.anim_next_exit);
		//		TranslateAnimation anim = new TranslateAnimation(
		//				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
		//				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
	}

	@Override
	public void go2Pre() {
		
	}

}
