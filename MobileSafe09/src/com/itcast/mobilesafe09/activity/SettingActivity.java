package com.itcast.mobilesafe09.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itcast.mobilesafe09.R;
import com.itcast.mobilesafe09.global.GlobalConstants;
import com.itcast.mobilesafe09.service.AddressService;
import com.itcast.mobilesafe09.service.BlackNumberService;
import com.itcast.mobilesafe09.utils.PrefUtils;
import com.itcast.mobilesafe09.utils.ServiceStatusUtils;
import com.itcast.mobilesafe09.view.AddressDialog;
import com.itcast.mobilesafe09.view.SettingItemView;

/**
 * 设置页面
 */
public class SettingActivity extends Activity {

	private SettingItemView sivUpdate;
	private SettingItemView sivAddress;
	private SettingItemView sivSrlj;
	private SettingItemView sivAddressStyle;

	//	private SharedPreferences mSP;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		sivUpdate = (SettingItemView) findViewById(R.id.siv_update);
		sivAddress = (SettingItemView) findViewById(R.id.siv_address);
		sivSrlj = (SettingItemView) findViewById(R.id.siv_srlj);
		sivAddressStyle = (SettingItemView) findViewById(R.id.siv_address_style);

		//修改自定义组合控件的标题和背景
		//		sivUpdate.setBackground(R.drawable.setting_first_selector);
		//		sivAddress.setBackground(R.drawable.setting_middle_selector);
		//		sivSrlj.setBackground(R.drawable.setting_last_selector);

		//		sivUpdate.setTitle("自动更新设置");
		//		sivAddress.setTitle("归属地显示设置");
		//		sivSrlj.setTitle("骚扰拦截设置");

		//		mSP = getSharedPreferences("config", MODE_PRIVATE);

		initUpdate();
		initAddress();
		initAddressStyle();
		initBlackNumber();
	}

	//两个作用:1. 页面创建时,onCreate->onStart, 用于数据回显
	//2. 从其他页面回来时, 用于服务状态开关更新
	@Override
	protected void onStart() {
		super.onStart();
		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this,
				AddressService.class);
		//System.out.println("serviceRunning:" + serviceRunning);

		//更新开关图片
		sivAddress.setToggleOn(serviceRunning);

		sivSrlj.setToggleOn(ServiceStatusUtils.isServiceRunning(this,
				BlackNumberService.class));
	}

	//黑名单拦截
	private void initBlackNumber() {
		sivSrlj.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//如果服务没有运行,启动服务;否则关闭服务
				if (!ServiceStatusUtils.isServiceRunning(
						getApplicationContext(), BlackNumberService.class)) {
					//启动归属地服务
					startService(new Intent(getApplicationContext(),
							BlackNumberService.class));

					sivSrlj.setToggleOn(true);
				} else {
					//停止服务
					stopService(new Intent(getApplicationContext(),
							BlackNumberService.class));

					sivSrlj.setToggleOn(false);
				}

			}
		});
	}

	//修改归属地样式
	private void initAddressStyle() {
		sivAddressStyle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//显示归属地样式弹窗
				final AddressDialog dialog = new AddressDialog(
						SettingActivity.this);

				//给自定义弹窗设置数据适配器
				dialog.setAdapter(new AddressStyleAdapter());

				//设置点击事件
				dialog.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						//将当前选中样式记录在本地sp中
						PrefUtils.putInt(getApplicationContext(),
								GlobalConstants.PREF_ADDRESS_STYLE, position);

						//隐藏弹窗
						dialog.dismiss();
					}
				});

				dialog.show();//显示弹窗
			}
		});
	}

	//归属地显示设置
	private void initAddress() {
		sivAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//如果服务没有运行,启动服务;否则关闭服务
				if (!ServiceStatusUtils.isServiceRunning(
						getApplicationContext(), AddressService.class)) {
					//启动归属地服务
					startService(new Intent(getApplicationContext(),
							AddressService.class));

					sivAddress.setToggleOn(true);
				} else {
					//停止服务
					stopService(new Intent(getApplicationContext(),
							AddressService.class));

					sivAddress.setToggleOn(false);
				}

			}
		});
	}

	//自动更新设置
	private void initUpdate() {
		//根据sp中保存的值更新开关状态
		boolean autoUpdate = PrefUtils.getBoolean(this,
				GlobalConstants.PREF_AUTO_UPDATE, true);
		sivUpdate.setToggleOn(autoUpdate);

		//给自动更新布局加点击事件
		sivUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//如果打开,则关闭; 反之亦然
				//				if (sivUpdate.isToggleOn()) {
				//					//关闭
				//					sivUpdate.setToggleOn(false);
				//				} else {
				//					//打开
				//					sivUpdate.setToggleOn(true);
				//				}
				//更新界面
				sivUpdate.toggle();
				//修改底层数据, 将当前开关状态记录在sp中
				//				mSP.edit().putBoolean("auto_update", sivUpdate.isToggleOn())
				//						.commit();
				PrefUtils.putBoolean(getApplicationContext(),
						GlobalConstants.PREF_AUTO_UPDATE,
						sivUpdate.isToggleOn());
			}
		});
	}

	private String[] mStyles = new String[] { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };

	//样式图片id集合
	private int[] mStyleIds = new int[] { R.drawable.shape_address_normal,
			R.drawable.shape_address_orange, R.drawable.shape_address_blue,
			R.drawable.shape_address_gray, R.drawable.shape_address_green };

	//归属地样式数据适配器
	class AddressStyleAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mStyles.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(SettingActivity.this,
					R.layout.item_address_style, null);

			ImageView ivPic = (ImageView) view.findViewById(R.id.iv_pic);
			TextView tvName = (TextView) view.findViewById(R.id.tv_name);
			ImageView ivSelect = (ImageView) view.findViewById(R.id.iv_select);

			ivPic.setImageResource(mStyleIds[position]);
			tvName.setText(mStyles[position]);

			//取出sp中记录的样式位置
			int pos = PrefUtils.getInt(getApplicationContext(),
					GlobalConstants.PREF_ADDRESS_STYLE, 0);

			//判断, 如果sp记录的样式位置和当前item位置一致, 说明当前item被选中
			if (pos == position) {
				ivSelect.setVisibility(View.VISIBLE);
			} else {
				//隐藏
				ivSelect.setVisibility(View.GONE);
			}

			return view;
		}

	}

}
