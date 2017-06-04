package com.itcast.mobilesafe09.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itcast.mobilesafe09.R;
import com.itcast.mobilesafe09.global.GlobalConstants;
import com.itcast.mobilesafe09.service.ProtectService;
import com.itcast.mobilesafe09.utils.PrefUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 
闪屏页 Splash

- 展示品牌 logo
- 初始化数据, 数据准备
- 合法性校验, 检测是否登录, 检测是否联网
- 版本更新, 自动更新

开发流程:

1. 写布局
2. 获取版本信息, PackageManager, 动态修改版本名的TextView
3. json的使用和创建, tomcat服务器配置
4. 使用HttpUtils请求服务器,获取版本相关信息
5. 使用JsonObject解析数据
6. 判断是否有更新, versionCode
7. 创建升级对话框, AlertDialog
8. 跳到主页面逻辑
		
		//1. 没有更新, 延迟2秒
		//2. 以后再说
		//3. 请求网络失败
		//4. json解析失败
		//5. 监听弹窗取消的事件
		//6. 取消安装

9. 渐变动画AlphaAnimation
10. 将项目打包成apk 选中项目->Properties->Android Tools->Export singed application package
11. 下载apk, HttpUtils 
12. 下载进度更新, ProgressDialog
13. 安装apk, 通过隐式意图跳到系统安装页面
14. 签名冲突问题, 比如将1.0重新打包后安装,2.0才能够成功覆盖
15. 细节处理

	1. 修改应用主题&去掉标题栏, 修改style.xml,  <item name="android:windowNoTitle">true</item>
	2. 监听弹窗取消的事件, 进入主页面
	3. 用户取消安装, 进入主页面, startActivityForResult
	
16. Context

	1. this, SplashActivity.this(内部类), 找的是当前Activity对象
	2. getApplicationContext(), 整个应用的上下文, 和Activity.this大部分情况可以通用
	
			个别情况: 
			1. 展示对话框,  new AlertDialog.Builder(this)
				activity是Context的子类, 子类拥有比父类更多的方法, 用Activity对象会更安全
		
			2. 加载布局: View.inflate(this, R.layout.xx, null);//尽量用Activity, 如果用getApplicationContext, 偶尔布局显示会出问题
 */
public class SplashActivity extends Activity {

	private TextView tvVersion;
	private RelativeLayout rlRoot;

	private String mVersionName;//服务器返回的版本名称
	private String mDes;//服务器返回的版本描述
	private String mDownloadUrl;//下载地址

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			enterHome();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		tvVersion = (TextView) findViewById(R.id.tv_version);
		tvVersion.setText(getVersionName());

		rlRoot = (RelativeLayout) findViewById(R.id.rl_root);

		//启动渐变动画
		AlphaAnimation anim = new AlphaAnimation(0.3f, 1);
		anim.setDuration(2000);//动画时间
		rlRoot.startAnimation(anim);

		//判断是否需要自动更新
		//		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		//		boolean autoUpdate = sp.getBoolean("auto_update", true);
		boolean autoUpdate = PrefUtils.getBoolean(this,
				GlobalConstants.PREF_AUTO_UPDATE, true);
		if (autoUpdate) {
			checkVerison();
		} else {
			//延时2秒跳主页面
			mHandler.sendEmptyMessageDelayed(0, 2000);
		}

		copyDb("address.db");//拷贝归属地数据库
		copyDb("commonnum.db");//拷贝常用号码数据库
		copyDb("antivirus.db");//拷贝手机病毒数据库

		installShortcut();

		//启动通知栏服务
		startService(new Intent(this, ProtectService.class));
	}

	//创建快捷方式(了解)
	//<uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT" />
	private void installShortcut() {
		//控制此方法只调用一次
		//如果一定调用过此方法,就不在调用
		boolean shortcutInstalled = PrefUtils.getBoolean(this,
				GlobalConstants.PREF_SHORTCUT, false);

		//名称
		//图标
		//动作
		//发系统广播来创建快捷方式
		/*
		 * <receiver
		 * android:name="com.android.launcher2.InstallShortcutReceiver"
		 * android:permission
		 * ="com.android.launcher.permission.INSTALL_SHORTCUT"> <intent-filter>
		 * <action android:name="com.android.launcher.action.INSTALL_SHORTCUT"
		 * /> </intent-filter> </receiver>
		 */

		if (!shortcutInstalled) {
			Intent intent = new Intent(
					"com.android.launcher.action.INSTALL_SHORTCUT");

			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "黑马小卫士");//名称
			intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory
					.decodeResource(getResources(), R.drawable.sjsd));//Bitmap对象,图标

			//跳到主页面的动作, 隐式意图
			Intent actionIntent = new Intent("com.itcast.mobilesafe09.HOME");
			actionIntent.addCategory(Intent.CATEGORY_DEFAULT);

			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);//动作

			sendBroadcast(intent);

			//标记已经创建过快捷方式
			PrefUtils.putBoolean(this, GlobalConstants.PREF_SHORTCUT, true);
		}
	}

	//获取版本名称
	private String getVersionName() {
		//包管理器
		PackageManager pm = getPackageManager();
		try {
			//获取版本信息
			//参1: 包名; 参2:标记, 传0表示只获取基本信息
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			String versionName = packageInfo.versionName;//版本名
			int versionCode = packageInfo.versionCode;//版本号

			System.out.println("版本名:" + versionName);
			System.out.println("版本号:" + versionCode);

			return versionName;
		} catch (NameNotFoundException e) {
			//包名未找到异常
			e.printStackTrace();
		}

		return "";
	}

	//获取版本号
	private int getVersionCode() {
		//包管理器
		PackageManager pm = getPackageManager();
		try {
			//获取版本信息
			//参1: 包名; 参2:标记, 传0表示只获取基本信息
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			int versionCode = packageInfo.versionCode;//版本号

			return versionCode;
		} catch (NameNotFoundException e) {
			//包名未找到异常
			e.printStackTrace();
		}

		return -1;
	}

	//检测版本更新
	//<uses-permission android:name="android.permission.INTERNET" />
	//<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	private void checkVerison() {
		//请求服务器,获取json, 并解析
		//HttpUrlConnection, HttpClient, AsyncHttpClient
		//第三方框架: XUtils, 综合框架, 对网络进行了封装
		HttpUtils utils = new HttpUtils();
		utils.configTimeout(2000);//设置超时时间

		//Genymotion: 10.0.3.2
		//eclipse模拟器: 10.0.2.2
		utils.send(HttpMethod.GET, "http://192.168.13.250:8080/update09.json",
				new RequestCallBack<String>() {

					//运行在主线程
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						//请求成功
						String result = responseInfo.result;//获取请求结果
						System.out.println("请求结果:" + result);

						//json解析
						try {
							//注意: 将json字符串通过构造方法传递过去
							JSONObject jo = new JSONObject(result);

							mVersionName = jo.getString("versionName");
							int versionCode = jo.getInt("versionCode");
							mDes = jo.getString("des");
							mDownloadUrl = jo.getString("downloadUrl");

							//System.out.println("解析结果:" + des);
							//客户端版本号和服务器进行比对
							if (getVersionCode() < versionCode) {
								//有更新
								//弹出升级提示对话框
								showUpdateDialog();
							} else {
								//没有更新
								//延迟两秒之后跳到主页面, ANR: application not response
								//使用handler发送延时消息
								//参1: what; 参2: 延时时间
								mHandler.sendEmptyMessageDelayed(0, 2000);
								//enterHome();
							}

						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(getApplicationContext(), "数据解析异常",
									Toast.LENGTH_SHORT).show();
							enterHome();
						}
					}

					//运行在主线程
					@Override
					public void onFailure(HttpException error, String msg) {
						//请求失败
						error.printStackTrace();
						Toast.makeText(getApplicationContext(), msg,
								Toast.LENGTH_SHORT).show();
						enterHome();
					}

				});
	}

	//弹出升级提示对话框
	protected void showUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("发现新版本:" + mVersionName);
		builder.setMessage(mDes);

		builder.setPositiveButton("立即更新",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						System.out.println("立即更新");
						//下载apk
						downloadApk();
					}

				});

		builder.setNegativeButton("以后再说",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//跳到主页面
						enterHome();
					}
				});

		//监听弹窗取消的事件
		//1. 点击返回键  2. 点击弹窗外侧
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				enterHome();
			}
		});

		//显示弹窗
		builder.show();
	}

	//下载apk
	protected void downloadApk() {
		//判断sdcard是否存在
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(getApplicationContext(), "没有找到sdcard!",
					Toast.LENGTH_SHORT).show();
			return;
		}

		//本地apk的文件地址
		String target = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/mobilesafe.apk";

		//进度弹窗
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//水平方向进度条, 用于展示进度
		dialog.setTitle("正在下载,请稍候...");
		dialog.show();

		HttpUtils utils = new HttpUtils();
		utils.download(mDownloadUrl, target, new RequestCallBack<File>() {

			@Override
			public void onSuccess(ResponseInfo<File> responseInfo) {
				//下载成功
				System.out.println("下载成功");

				//跳到安装页面进行安装
				installApk(responseInfo.result);
			}

			//下载进度
			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				super.onLoading(total, current, isUploading);
				int percent = (int) (current * 100 / total);
				//更新进度弹窗
				dialog.setProgress(percent);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				//下载失败
				error.printStackTrace();
				Toast.makeText(SplashActivity.this, "下载失败!", Toast.LENGTH_SHORT)
						.show();
			}

		});
	}

	//安装apk
	protected void installApk(File file) {
		//		<intent-filter>
		//        <action android:name="android.intent.action.VIEW" />
		//        <category android:name="android.intent.category.DEFAULT" />
		//        <data android:scheme="content" />
		//        <data android:scheme="file" />
		//        <data android:mimeType="application/vnd.android.package-archive" />
		//    </intent-filter>
		Intent intent = new Intent(Intent.ACTION_VIEW);
		//设置数据和类型
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");

		//需要返回结果
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//如果进入此方法, 说明用户取消安装了
		//进入主页面
		enterHome();
	}

	//跳到主页面
	//1. 没有更新, 延迟2秒
	//2. 以后再说
	//3. 请求网络失败
	//4. json解析失败
	//5. 监听弹窗取消的事件
	//6. 取消安装
	protected void enterHome() {
		startActivity(new Intent(this, HomeActivity.class));
		finish();
	}

	//拷贝数据库
	//从assets拷贝到本地路径data/data
	public void copyDb(String dbName) {
		File dir = getFilesDir();//获取data/data路径; data/data/包名/files/
		File desFile = new File(dir, dbName);

		if (desFile.exists()) {
			System.out.println("数据库" + dbName + "已经存在,无需拷贝!");
			return;
		}

		FileOutputStream out = null;
		InputStream in = null;
		try {
			out = new FileOutputStream(desFile);
			in = getAssets().open(dbName);

			int len = 0;
			byte[] buffer = new byte[1024 * 8];
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println("拷贝" + dbName + "成功!");

	}

}
