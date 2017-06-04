package com.itcast.mobilesafe09.activity;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

import com.itcast.mobilesafe09.R;
import com.itcast.mobilesafe09.service.AppLockService;
import com.itcast.mobilesafe09.utils.ServiceStatusUtils;
import com.itcast.mobilesafe09.utils.SmsUtils;
import com.itcast.mobilesafe09.utils.SmsUtils.OnSmsListener;
import com.itcast.mobilesafe09.utils.ToastUtils;
import com.itcast.mobilesafe09.view.SettingItemView;

/**
 * 常用工具
 */
public class CommonToolsActivity extends Activity implements OnClickListener {

	private ProgressBar pbProgress;
	private SettingItemView sivApplockService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_tools);

		pbProgress = (ProgressBar) findViewById(R.id.pb_progress);

		findViewById(R.id.siv_address).setOnClickListener(this);
		findViewById(R.id.siv_common_number).setOnClickListener(this);
		findViewById(R.id.siv_sms_backup).setOnClickListener(this);
		findViewById(R.id.siv_sms_restore).setOnClickListener(this);
		findViewById(R.id.siv_applock).setOnClickListener(this);

		sivApplockService = (SettingItemView) findViewById(R.id.siv_applock_service);
		sivApplockService.setOnClickListener(this);
		
		//制造崩溃
		int i = 1/0;
	}

	@Override
	protected void onStart() {
		super.onStart();
		//根据程序锁服务是否运行,来更新开关状态
		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this,
				AppLockService.class);
		sivApplockService.setToggleOn(serviceRunning);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.siv_address:
			startActivity(new Intent(this, AddressActivity.class));
			break;
		case R.id.siv_common_number:
			startActivity(new Intent(this, CommonNumberActivity.class));
			break;
		case R.id.siv_sms_backup:
			//短信备份
			smsBackup();
			break;
		case R.id.siv_sms_restore:
			//短信还原
			smsRestore();
			break;
		case R.id.siv_applock:
			startActivity(new Intent(this, AppLockActivity.class));
			break;
		case R.id.siv_applock_service:
			//跳到辅助功能页面
			Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	//短信还原
	private void smsRestore() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			ToastUtils.showToast(this, "sdcard不存在!");
			return;
		}

		final File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/sms09.json");

		//展示进度弹窗
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setTitle("正在还原,请稍候...");
		dialog.show();

		new Thread() {
			public void run() {
				try {
					SmsUtils.smsRestore(getApplicationContext(), file,
							new OnSmsListener() {

								@Override
								public void onSmsTotal(int totalCount) {
									dialog.setMax(totalCount);
								}

								@Override
								public void onSmsProgress(int progress) {
									dialog.setProgress(progress);
								}
							});

					dialog.dismiss();

					ToastUtils.showToastOnUIThread(CommonToolsActivity.this,
							"还原成功!");
				} catch (Exception e) {
					e.printStackTrace();
					ToastUtils.showToastOnUIThread(CommonToolsActivity.this,
							"还原失败!");
				}

			};
		}.start();
	}

	//短信备份
	private void smsBackup() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			ToastUtils.showToast(this, "sdcard不存在!");
			return;
		}

		final File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/sms09.json");

		//展示进度弹窗
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setTitle("正在备份,请稍候...");
		dialog.show();

		new Thread() {
			public void run() {
				try {
					SmsUtils.smsBackup(getApplicationContext(), file,
							new OnSmsListener() {

								//获取到了短信总数
								@Override
								public void onSmsTotal(int totalCount) {
									dialog.setMax(totalCount);
									//pbProgress.setMax(totalCount);
								}

								//获取到了短信的进度
								@Override
								public void onSmsProgress(int progress) {
									dialog.setProgress(progress);
									//pbProgress.setProgress(progress);
								}
							});

					ToastUtils.showToastOnUIThread(CommonToolsActivity.this,
							"备份成功!");

					dialog.dismiss();
				} catch (Exception e) {
					e.printStackTrace();
					ToastUtils.showToastOnUIThread(CommonToolsActivity.this,
							"备份失败!");
				}
			};
		}.start();

	}

}
