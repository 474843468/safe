package com.itcast.mobilesafe09.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.itcast.mobilesafe09.R;
import com.itcast.mobilesafe09.global.GlobalConstants;
import com.itcast.mobilesafe09.utils.ToastUtils;

/**
 * 程序锁输入密码页面
 * 
 * 1. 用户不知道密码时: 拦截物理返回键,跳到桌面
 * 2. 用户知道密码时: 发送广播,通知跳过当前包的验证
 * 
 * 单例启动模式(4种启动模式,面试高发)
 * <activity
            android:name="com.itcast.mobilesafe09.activity.EnterPwdActivity"
            android:launchMode="singleInstance" >
    </activity>
    
   当前页面启动之后, 不会出现在最近任务列表中 android:excludeFromRecents(非重点)
     <activity
            android:name="com.itcast.mobilesafe09.activity.EnterPwdActivity"
            android:excludeFromRecents="true"
            android:launchMode="singleInstance" >
     </activity>
 */
public class EnterPwdActivity extends Activity {

	private ImageView ivIcon;
	private TextView tvName;
	private EditText etPwd;
	private Button btnOk;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_pwd);

		ivIcon = (ImageView) findViewById(R.id.iv_icon);
		tvName = (TextView) findViewById(R.id.tv_name);
		etPwd = (EditText) findViewById(R.id.et_pwd);
		btnOk = (Button) findViewById(R.id.btn_ok);

		final String packageName = getIntent().getStringExtra("packageName");

		//根据包名获取app信息
		PackageManager pm = getPackageManager();
		try {
			ApplicationInfo applicationInfo = pm.getApplicationInfo(
					packageName, 0);

			String name = applicationInfo.loadLabel(pm).toString();//名称
			Drawable icon = applicationInfo.loadIcon(pm);//图标

			ivIcon.setImageDrawable(icon);
			tvName.setText(name);

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String pwd = etPwd.getText().toString();

				if (!TextUtils.isEmpty(pwd)) {
					//密码校验
					if ("123".equals(pwd)) {
						//通知服务,跳过当前app的验证
						//发广播
						Intent intent = new Intent(
								GlobalConstants.ACTION_SKIP_PACKAGE);
						intent.putExtra("packageName", packageName);
						sendBroadcast(intent);

						//销毁当前页面
						finish();
					} else {
						ToastUtils.showToast(getApplicationContext(), "密码错误!");
					}
				} else {
					ToastUtils.showToast(getApplicationContext(), "输入密码不能为空!");
				}

			}
		});
	}

	//拦截物理返回键
	@Override
	public void onBackPressed() {
		//跳到桌面
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);

		finish();
	}

}
