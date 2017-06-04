package com.itcast.mobilesafe09.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.itcast.mobilesafe09.R;
import com.itcast.mobilesafe09.global.GlobalConstants;
import com.itcast.mobilesafe09.utils.PrefUtils;
import com.itcast.mobilesafe09.utils.ToastUtils;

/**
 * 手机防盗-设置向导3
 */
public class Setup3Activity extends BaseSetupActivity {

	private EditText etPhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);

		etPhone = (EditText) findViewById(R.id.et_phone);

		//安全号码回显
		String phone = PrefUtils.getString(this,
				GlobalConstants.PREF_SAFE_PHONE, "");
		etPhone.setText(phone);
	}

	@Override
	public void go2Next() {
		//保存电话号码
		String phone = etPhone.getText().toString().trim();

		if (TextUtils.isEmpty(phone)) {
			ToastUtils.showToast(this, "输入号码不能为空!");
			return;
		}

		//保存安全号码
		PrefUtils.putString(this, GlobalConstants.PREF_SAFE_PHONE, phone);

		startActivity(new Intent(this, Setup4Activity.class));
		finish();

		//切换activity动画
		//参1: 进入动画; 参2:退出动画
		overridePendingTransition(R.anim.anim_next_enter, R.anim.anim_next_exit);
	}

	@Override
	public void go2Pre() {
		startActivity(new Intent(this, Setup2Activity.class));
		finish();
		//切换activity动画
		//参1: 进入动画; 参2:退出动画
		overridePendingTransition(R.anim.anim_pre_enter, R.anim.anim_pre_exit);
	}

	//选择联系人
	public void selectContact(View view) {
		//跳到选择联系人页面
		startActivityForResult(new Intent(this, SelectContactActivity.class),
				99);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//请求码requestCode: 区分是哪个请求过来的; startActivityForResult
		System.out.println("requestCode:" + requestCode);
		//结果码resultCode: 返回结果的状态, 可以用来判断请求是否成功; setResult
		System.out.println("resultCode:" + resultCode);

		if (resultCode == RESULT_OK) {//请求成功
			String phone = data.getStringExtra("phone");

			//过滤掉短杠和空格
			phone = phone.replaceAll("-", "").replaceAll(" ", "").trim();

			etPhone.setText(phone);
		}

	}

}
