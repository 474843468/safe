package com.itcast.mobilesafe09.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itcast.mobilesafe09.R;
import com.itcast.mobilesafe09.global.GlobalConstants;
import com.itcast.mobilesafe09.utils.PrefUtils;

/**
 * 手机防盗主页面
 */
public class LostFindActivity extends Activity {

	private TextView tvPhone;
	private ImageView ivProtect;
	private RelativeLayout rlProtect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lost_find);

		tvPhone = (TextView) findViewById(R.id.tv_phone);
		ivProtect = (ImageView) findViewById(R.id.iv_protect);
		rlProtect = (RelativeLayout) findViewById(R.id.rl_protect);

		//回显安全号码
		String phone = PrefUtils.getString(this,
				GlobalConstants.PREF_SAFE_PHONE, "");
		tvPhone.setText(phone);

		//回显防盗保护总开关
		boolean protect = PrefUtils.getBoolean(this,
				GlobalConstants.PREF_PROTECT, false);
		ivProtect.setImageResource(protect ? R.drawable.lock
				: R.drawable.unlock);

		//点击防盗保护开关, 切换状态
		rlProtect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean protect = PrefUtils.getBoolean(getApplicationContext(),
						GlobalConstants.PREF_PROTECT, false);

				//修改sp, 取反
				PrefUtils.putBoolean(getApplicationContext(),
						GlobalConstants.PREF_PROTECT, !protect);

				//更新开关图片, 取反
				ivProtect.setImageResource(protect ? R.drawable.unlock
						: R.drawable.lock);
			}
		});
	}

	//重新进入设置向导
	// android:clickable="true",TextView原生不支持点击, 需要加android:clickable属性才可以
	// android:onClick="reEnterSetup"
	public void reEnterSetup(View view) {
		startActivity(new Intent(this, Setup1Activity.class));
		finish();
	}

}
