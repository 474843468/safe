package com.itcast.mobilesafe09.activity;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.itcast.mobilesafe09.R;
import com.itcast.mobilesafe09.bean.AppInfo;
import com.itcast.mobilesafe09.engine.AppInfoProvider;
import com.itcast.mobilesafe09.utils.ToastUtils;
import com.itcast.mobilesafe09.view.ProgressView;

/**
 * 软件管理
 * 
 * 开发流程:
 * 1. 布局开发, 自定义进度条, progressDrawable
 * 2. 自定义进度条组合控件, ProgressView
 * 3. 获取存储空间信息, 内部存储+外部存储
 * 4. 获取已安装app列表, PackageManager, AppInfoProvider
 * 5. 使用ListView展示app列表(基本使用)
 * 6. 状态机判断应用类型及安装位置
 * 7. 修改软件安装位置(了解),用原生模拟器演示
 * 
 * 			<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    			android:installLocation="internalOnly"//只能安装在手机内存, 默认值(无法移动到sdcard)
    			android:installLocation="preferExternal"//优先安装在sdcard;如果sdcard安装失败,安装在手机内存
    			android:installLocation="auto"//优先安装在手机内存,其次考虑sdcard
   			>
   	8. ListView高级用法: 复杂ListView展示
   	
   		8.1 区分展示系统应用和用户应用, 用户应用展示在上边
   		
   			8.1.1 创建两个集合,分别保存用户和系统app对象
   			8.1.2 adapter修改getCount方法和getItem方法, 注意getItem方法
   			
   		8.2 展示标题栏布局, 普通布局+标题栏布局
   		
   			8.2.1 重写getCount,加2
   			8.2.2 重写getItem, 一定注意ListView的item位置和ArrayList对象位置的对应关系, 可以画图理清思路
   			8.2.3重写getViewTypeCount, 返回布局类型个数
   			8.2.4重写getItemViewType,返回某个位置的布局类型, 此时定义两种类型TYPE_TITLE和TYPE_NORMAL
   			8.2.5重写getView方法, 根据布局类型,加载不同布局. 具体加载布局方式和普通ListView完全一样
   			
   		8.3 注意: 1. 布局类型必须从0开始计数, TYPE_TITLE, TYPE_NORMAL
   				2. 展示复杂ListView, 核心方法: getViewTypeCount, getItemViewType, getView
   				
   	9. 常驻浮窗, 帧布局, 监听ListView滑动事件,修改TextView内容
   	10. 点击条目,显示弹窗, PopupWindow
   	
   			10.1 显示PopupWindow, 调整位置
   			10.2 弹窗动画效果, Style, anim xml
   			10.3 细节优化和bug处理
   			10.4 弹窗选项加点击事件
   			
   	11. 卸载: 跳到系统卸载页面; 数据回显onActivityResult; 系统app无法卸载
   	12. 启动: PackageManager获取app的入口intent
   	13. 分享: 系统分享实现, 隐式意图
   	14. 信息: 跳到系统信息页
 */
public class AppManagerActivity extends Activity implements OnClickListener {

	private static final int TYPE_TITLE = 0;//标题栏类型
	private static final int TYPE_NORMAL = 1;//普通类型

	private ProgressView pvRom;
	private ProgressView pvSdcard;

	private ListView lvList;
	private TextView tvTitle;

	//private ArrayList<AppInfo> mList;
	private ArrayList<AppInfo> mUserList;
	private ArrayList<AppInfo> mSystemList;
	private PopupWindow mPopup;
	private AppInfoAdapter mAdapter;

	private AppInfo mCurrentInfo;//记录当前被点击的对象

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);

		pvRom = (ProgressView) findViewById(R.id.pv_rom);
		pvSdcard = (ProgressView) findViewById(R.id.pv_sdcard);
		lvList = (ListView) findViewById(R.id.lv_list);
		tvTitle = (TextView) findViewById(R.id.tv_app_title);

		pvRom.setTitle("内部存储:");
		pvSdcard.setTitle("外部存储:");

		initSpaceInfo();

		initData();

		lvList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			//滑动事件监听
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				//firstVisibleItem: 当前显示的第一个item的位置
				//System.out.println("firstVisibleItem:" + firstVisibleItem);

				if (firstVisibleItem > mUserList.size()) {
					//切换为系统应用
					tvTitle.setText("系统应用(" + mSystemList.size() + ")");
				} else {
					//切换为用户应用
					tvTitle.setText("用户应用(" + mUserList.size() + ")");
				}

			}
		});

		lvList.setOnItemClickListener(new OnItemClickListener() {

			//View view:当前被点击的条目对象
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//普通条目才显示弹窗
				AppInfo info = mAdapter.getItem(position);

				if (info != null) {
					mCurrentInfo = info;
					showPopup(view);
				}
			}
		});
	}

	//显示小弹窗
	protected void showPopup(View view) {
		if (mPopup == null) {//PopupWindow只需要初始化一次
			View contentView = View.inflate(this, R.layout.popup_appinfo, null);

			//设置点击事件
			contentView.findViewById(R.id.tv_uninstall)
					.setOnClickListener(this);
			contentView.findViewById(R.id.tv_open).setOnClickListener(this);
			contentView.findViewById(R.id.tv_share).setOnClickListener(this);
			contentView.findViewById(R.id.tv_info).setOnClickListener(this);

			//宽高包裹内容
			mPopup = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, true);

			//给弹窗设置背景
			//只有设置了背景之后, 点击返回键或者弹窗外侧, 弹窗才会消失
			mPopup.setBackgroundDrawable(new ColorDrawable());

			//设置弹窗动画
			mPopup.setAnimationStyle(R.style.PopupWindowAnim);
		}

		//显示在条目对象的下方
		//向上移动一个条目高度
		mPopup.showAsDropDown(view, 60, -view.getHeight());
	}

	private void initData() {
		//获取所有app集合
		ArrayList<AppInfo> list = AppInfoProvider.getInstalledAppList(this);

		//区分用户和系统app
		mUserList = new ArrayList<AppInfo>();
		mSystemList = new ArrayList<AppInfo>();
		for (AppInfo appInfo : list) {
			if (appInfo.isUserApp) {
				//用户app
				mUserList.add(appInfo);
			} else {
				//系统app
				mSystemList.add(appInfo);
			}
		}

		mAdapter = new AppInfoAdapter();
		lvList.setAdapter(mAdapter);
	}

	//初始化存储空间信息
	private void initSpaceInfo() {
		//内部存储
		//路径: /data
		File dir = Environment.getDataDirectory();//获取内部存储文件夹
		long totalSpace = dir.getTotalSpace();//总空间
		long freeSpace = dir.getFreeSpace();//剩余空间
		long usedSpace = totalSpace - freeSpace;//已用空间

		//计算进度 = 已用空间/总空间 * 100
		int progress = (int) (usedSpace * 100 / totalSpace);
		pvRom.setProgress(progress);

		//将字节转化为带有单位的大小,MB,GB等
		//Formatter.formatFileSize(this, 123124124);
		pvRom.setLeftText(Formatter.formatFileSize(this, usedSpace) + "已用");
		pvRom.setRightText(Formatter.formatFileSize(this, freeSpace) + "可用");

		//外部存储
		File sdcardDir = Environment.getExternalStorageDirectory();
		long sdcardTotal = sdcardDir.getTotalSpace();
		long sdcardFree = sdcardDir.getFreeSpace();

		long sdcardUsed = sdcardTotal - sdcardFree;

		int sdcardProgress = (int) (sdcardUsed * 100 / sdcardTotal);
		pvSdcard.setProgress(sdcardProgress);

		pvSdcard.setLeftText(Formatter.formatFileSize(this, sdcardUsed) + "已用");
		pvSdcard.setRightText(Formatter.formatFileSize(this, sdcardFree) + "可用");
	}

	class AppInfoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			//return mList.size();
			return mUserList.size() + mSystemList.size() + 2;//增加两个标题栏数量
		}

		@Override
		public AppInfo getItem(int position) {
			//判断是否是标题栏
			if (position == 0 || position == mUserList.size() + 1) {
				return null;
			}

			if (position < mUserList.size() + 1) {//加1: 多了一个标题栏,会占位
				return mUserList.get(position - 1);//减掉标题栏占位
			} else {
				return mSystemList.get(position - mUserList.size() - 2);//减掉两个标题栏占位
			}
			//return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		//返回布局类型个数
		@Override
		public int getViewTypeCount() {
			return 2;
		}

		//返回某个位置的布局类型
		@Override
		public int getItemViewType(int position) {
			//判断是否是标题栏
			if (position == 0 || position == mUserList.size() + 1) {
				return TYPE_TITLE;
			} else {
				return TYPE_NORMAL;
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//获取当前位置布局类型
			int type = getItemViewType(position);

			//根据不同类型,加载不同布局
			switch (type) {
			case TYPE_NORMAL:
				ViewHolder holder;
				if (convertView == null) {
					convertView = View.inflate(AppManagerActivity.this,
							R.layout.item_appinfo, null);

					holder = new ViewHolder();
					holder.ivIcon = (ImageView) convertView
							.findViewById(R.id.iv_icon);
					holder.tvName = (TextView) convertView
							.findViewById(R.id.tv_name);
					holder.tvLocation = (TextView) convertView
							.findViewById(R.id.tv_location);
					holder.tvSize = (TextView) convertView
							.findViewById(R.id.tv_size);

					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}

				AppInfo info = getItem(position);

				holder.ivIcon.setImageDrawable(info.icon);
				holder.tvName.setText(info.name);
				holder.tvSize.setText(Formatter.formatFileSize(
						getApplicationContext(), info.size));

				holder.tvLocation.setText(info.isRom ? "手机内存" : "外置存储卡");
				break;
			case TYPE_TITLE:
				TitleHolder tHolder;
				if (convertView == null) {
					convertView = View.inflate(AppManagerActivity.this,
							R.layout.item_title, null);

					tHolder = new TitleHolder();
					tHolder.tvTitle = (TextView) convertView
							.findViewById(R.id.tv_title);
					convertView.setTag(tHolder);
				} else {
					tHolder = (TitleHolder) convertView.getTag();
				}

				if (position == 0) {
					//用户应用
					tHolder.tvTitle.setText("用户应用(" + mUserList.size() + ")");
				} else {
					//系统应用
					tHolder.tvTitle.setText("系统应用(" + mSystemList.size() + ")");
				}

				break;

			default:
				break;
			}

			return convertView;
		}

	}

	static class ViewHolder {
		public ImageView ivIcon;
		public TextView tvName;
		public TextView tvLocation;
		public TextView tvSize;
	}

	static class TitleHolder {
		public TextView tvTitle;
	}

	@Override
	public void onClick(View v) {
		//隐藏弹窗
		mPopup.dismiss();

		switch (v.getId()) {
		case R.id.tv_uninstall:
			//卸载
			System.out.println("卸载");

			//系统应用无法卸载
			if (mCurrentInfo.isUserApp) {
				Intent i = new Intent();
				i.setAction(Intent.ACTION_DELETE);//设置我们要执行的卸载动作
				i.setData(Uri.parse("package:" + mCurrentInfo.packageName));//设置获取到的URI
				startActivityForResult(i, 0);
			} else {
				ToastUtils.showToast(this, "系统应用无法卸载!");
			}
			break;
		case R.id.tv_open:
			//打开
			System.out.println("打开");

			PackageManager pm = getPackageManager();
			//获取某个app的入口的启动页面
			Intent intent = pm
					.getLaunchIntentForPackage(mCurrentInfo.packageName);

			if (intent != null) {
				startActivity(intent);
			} else {
				ToastUtils.showToast(this, "无法启动该应用!");
			}
			break;
		case R.id.tv_share:
			//分享
			//发送隐式意图, 系统就会展示出所有已安装的可以分享该内容的app供用户选择
			System.out.println("分享");

			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("text/plain");//设置数据类型, 纯文本
			shareIntent.putExtra(Intent.EXTRA_TEXT,
					"发现一个很好的应用哦! 下载地址:https://play.google.com/store/apps/details?id="
							+ mCurrentInfo.packageName);
			startActivity(shareIntent);

			break;
		case R.id.tv_info:
			//信息
			System.out.println("信息");

			//跳到应用信息页, 隐式意图
			Intent infoIntent = new Intent();
			infoIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			infoIntent
					.setData(Uri.parse("package:" + mCurrentInfo.packageName));
			startActivity(infoIntent);
			break;

		default:
			break;
		}
	}

	//卸载结束后会走此方法
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("resultCode:" + resultCode);

		//此处无法区分卸载是否成功, 因为resultCode都是0, 所以重新拉取数据
		//重新加载数据, 刷新页面
		initData();
	}

}
