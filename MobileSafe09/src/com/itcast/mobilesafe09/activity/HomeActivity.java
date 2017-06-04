package com.itcast.mobilesafe09.activity;

import java.util.ArrayList;

import net.youmi.android.AdManager;
import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itcast.mobilesafe09.R;
import com.itcast.mobilesafe09.bean.HomeItemInfo;
import com.itcast.mobilesafe09.global.GlobalConstants;
import com.itcast.mobilesafe09.utils.MD5Utils;
import com.itcast.mobilesafe09.utils.PrefUtils;
import com.itcast.mobilesafe09.utils.ToastUtils;

/**
 * 主页面
 * 
 * 开发流程:
 * 1. 手机卫士头布局
 * 
 * 		 android:textStyle="bold" 加粗
 * 		 android:scaleType="center": 修改ImageView缩放方式
 * 
 * 2. logo的属性动画
 * 3. GridView布局配置
 * 4. 初始化数据, HomeItemInfo
 * 5. 数据适配器Adapter
 * 6. 布局细节微调,  android:horizontalSpacing="3dp", android:verticalSpacing="3dp"
 * 7. 状态选择器, 条目, 设置按钮(android:clickable="true")
 * 8. 点击设置按钮,跳到设置页面
 * 9. 手机防盗弹窗开发, 自定义dialog布局, 密码验证逻辑
 * 10. 密码进行md5加密
 */
public class HomeActivity extends Activity {

	private ImageView ivLogo;
	private GridView gvHome;
	private ImageView ivSetting;

	private String[] items = new String[] { "手机防盗", "通讯卫士", "软件管理", "进程管理",
			"流量统计", "手机杀毒", "缓存清理", "高级工具" };

	private Integer[] icons = new Integer[] { R.drawable.sjfd, R.drawable.srlj,
			R.drawable.rjgj, R.drawable.jcgl, R.drawable.lltj, R.drawable.sjsd,
			R.drawable.hcql, R.drawable.cygj };

	private String[] descs = new String[] { "远程定位手机", "全面拦截骚扰", "管理您的软件",
			"管理运行进程", "流量一目了然", "病毒无处藏身", "系统快如火箭", "工具大全" };
	private ArrayList<HomeItemInfo> mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		//初始化有米广告sdk
		AdManager.getInstance(this).init("91d29341640c81c7",
				"29066bbd039e1dda", true);

		ivLogo = (ImageView) findViewById(R.id.iv_logo);
		gvHome = (GridView) findViewById(R.id.gv_home);
		ivSetting = (ImageView) findViewById(R.id.iv_setting);

		//属性动画, 沿着y轴旋转
		//ivLogo.setRotationY(90);
		//参1:动画的对象; 参2:属性名;参3...: 变化范围
		ObjectAnimator anim = ObjectAnimator.ofFloat(ivLogo, "rotationY", 0,
				360);
		anim.setDuration(2000);//动画时间
		anim.setRepeatMode(ObjectAnimator.REVERSE);//正向执行一遍之后再反向执行
		anim.setRepeatCount(ObjectAnimator.INFINITE);//无限循环
		anim.start();//启动动画

		//FocusedTextView view = new FocusedTextView(this);

		initData();

		//		ivSetting.setOnClickListener(new OnClickListener() {
		//			
		//			@Override
		//			public void onClick(View v) {
		//			}
		//		});

		//
		gvHome.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					//手机防盗
					showPasswordDialog();
					break;
				case 1:
					//黑名单管理
					startActivity(new Intent(getApplicationContext(),
							BlackNumberActivity.class));
					break;
				case 2:
					//软件管理
					startActivity(new Intent(getApplicationContext(),
							AppManagerActivity.class));
					break;
				case 3:
					//进程管理
					startActivity(new Intent(getApplicationContext(),
							ProcessManagerActivity.class));
					break;
				case 4:
					//流量统计
					startActivity(new Intent(getApplicationContext(),
							TrafficStatActivity.class));
					break;
				case 5:
					//手机杀毒
					startActivity(new Intent(getApplicationContext(),
							AntiVirusActivity.class));
					break;
				case 6:
					//缓存清理
					startActivity(new Intent(getApplicationContext(),
							CleanCacheActivity.class));
					break;
				case 7:
					//高级工具
					startActivity(new Intent(getApplicationContext(),
							CommonToolsActivity.class));
					break;

				default:
					break;
				}
			}
		});

		initAd();
	}

	//初始化广告条
	private void initAd() {
		// 实例化广告条
		AdView adView = new AdView(this, AdSize.FIT_SCREEN);
		// 获取要嵌入广告条的布局
		LinearLayout adLayout = (LinearLayout) findViewById(R.id.adLayout);

		// 将广告条加入到布局中
		adLayout.addView(adView);
	}

	//初始化数据
	private void initData() {
		//初始化8个item的数据
		mList = new ArrayList<HomeItemInfo>();
		for (int i = 0; i < items.length; i++) {
			HomeItemInfo info = new HomeItemInfo();
			info.imageId = icons[i];
			info.title = items[i];
			info.des = descs[i];

			mList.add(info);
		}

		//给GridView设置数据 
		gvHome.setAdapter(new HomeAdapter());
	}

	//数据适配器, 和ListView完全一样
	class HomeAdapter extends BaseAdapter {

		//返回条目数量
		@Override
		public int getCount() {
			return mList.size();
		}

		//返回条目对象
		@Override
		public HomeItemInfo getItem(int position) {
			return mList.get(position);
		}

		//返回条目的id
		@Override
		public long getItemId(int position) {
			return position;
		}

		//初始化条目布局
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//初始化布局
			View view = View.inflate(HomeActivity.this, R.layout.item_home,
					null);

			//初始化控件
			TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
			TextView tvDes = (TextView) view.findViewById(R.id.tv_des);
			ImageView ivIcon = (ImageView) view.findViewById(R.id.iv_icon);

			//获取当前对象
			HomeItemInfo info = getItem(position);

			tvTitle.setText(info.title);
			tvDes.setText(info.des);
			//给ImageView设置图片, src属性
			ivIcon.setImageResource(info.imageId);

			return view;
		}

	}

	//进入设置页面
	public void goToSetting(View view) {
		startActivity(new Intent(this, SettingActivity.class));
	}

	//显示密码弹窗
	protected void showPasswordDialog() {
		//如果没有设置过密码, 显示设置密码弹窗, 否则显示输入密码弹窗
		String savePwd = PrefUtils.getString(this,
				GlobalConstants.PREF_PASSWORD, null);

		if (TextUtils.isEmpty(savePwd)) {
			showSetPwdDialog();
		} else {
			showInputPwdDialog();
		}
	}

	//输入密码弹窗
	private void showInputPwdDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		//初始化弹窗自定义布局
		View view = View.inflate(this, R.layout.dialog_input_pwd, null);

		//给弹窗设置自定义布局
		builder.setView(view);

		//创建一个弹窗对象
		final AlertDialog dialog = builder.create();

		final EditText etPwd = (EditText) view.findViewById(R.id.et_pwd);
		Button btnOk = (Button) view.findViewById(R.id.btn_ok);
		Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//1. 非空判断
				//2. 校验密码是否正确
				String pwd = etPwd.getText().toString().trim();

				if (!TextUtils.isEmpty(pwd)) {
					//取出保存的密码
					String savePwd = PrefUtils.getString(
							getApplicationContext(),
							GlobalConstants.PREF_PASSWORD, null);

					if (MD5Utils.getMd5(pwd).equals(savePwd)) {
						//密码正确
						ToastUtils.showToast(getApplicationContext(), "登录成功!");
						dialog.dismiss();

						//跳到手机防盗页面
						goToLostFind();
					} else {
						ToastUtils.showToast(getApplicationContext(), "密码错误!");
					}

				} else {
					ToastUtils.showToast(getApplicationContext(), "输入密码不能为空!");
				}
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		//builder.show();
		dialog.show();
	}

	//显示设置密码弹窗
	private void showSetPwdDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		//初始化弹窗自定义布局
		View view = View.inflate(this, R.layout.dialog_set_pwd, null);

		//给弹窗设置自定义布局
		builder.setView(view);

		//创建一个弹窗对象
		final AlertDialog dialog = builder.create();

		final EditText etPwd = (EditText) view.findViewById(R.id.et_pwd);
		final EditText etPwdConfirm = (EditText) view
				.findViewById(R.id.et_pwd_confirm);
		Button btnOk = (Button) view.findViewById(R.id.btn_ok);
		Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//1. 非空判断
				//2. 校验密码是否一致
				//3. 保存密码
				String pwd = etPwd.getText().toString().trim();
				String pwdConfirm = etPwdConfirm.getText().toString().trim();

				if (!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(pwdConfirm)) {
					if (pwd.equals(pwdConfirm)) {
						//保存密码 sp
						PrefUtils.putString(getApplicationContext(),
								GlobalConstants.PREF_PASSWORD,
								MD5Utils.getMd5(pwd));
						//让弹窗消失
						dialog.dismiss();

						//跳到手机防盗页面
						goToLostFind();
					} else {
						ToastUtils.showToast(getApplicationContext(),
								"两次密码不一致!");
					}
				} else {
					ToastUtils.showToast(getApplicationContext(), "输入密码不能为空!");
				}
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		//builder.show();
		dialog.show();
	}

	//手机防盗
	protected void goToLostFind() {
		//如果第一次进入, 跳到设置向导页面进行设置
		//如果设置完成, 直接进入手机防盗主页面
		boolean config = PrefUtils.getBoolean(this,
				GlobalConstants.PREF_CONFIG, false);

		if (!config) {
			//跳到向导页
			startActivity(new Intent(this, Setup1Activity.class));
		} else {
			//手机防盗主页
			startActivity(new Intent(this, LostFindActivity.class));
		}

	}
}
