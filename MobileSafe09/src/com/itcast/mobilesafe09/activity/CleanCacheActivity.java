package com.itcast.mobilesafe09.activity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itcast.mobilesafe09.R;
import com.itcast.mobilesafe09.utils.ToastUtils;

/**
 * 缓存清理
 * 
 * 开发流程:
 * 
 * 1. 异步扫描所有app,获取缓存
 * 2. 初始化缓存对象集合, 并添加对象
 * 3. ListView数据适配器开发
 * 4. ListView界面展示
 * 5. 头布局的开发
 * 6. 扫描动画开发
 * 7. 扫描进度更新
 * 8. 缓存扫描结果展示
 * 9. 重新扫描
 * 10. 停止异步任务&强制竖屏
 * 11. 立即清理功能开发
 * 12.清理单个app缓存
 */
public class CleanCacheActivity extends Activity {

	private ListView lvList;
	private PackageManager mPM;

	private ArrayList<CacheInfo> mList;
	private ArrayList<CacheInfo> mCacheList;//有缓存的app集合

	private CacheAdapter mAdapter;
	private CacheTask mTask;

	private ImageView ivLine;
	private ImageView ivIcon;
	private TextView tvName;
	private TextView tvCacheSize;
	private TextView tvResult;
	private ProgressBar pbProgress;
	private LinearLayout llProgress;
	private LinearLayout llResult;
	private Button btnCleanAll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_cache);

		mPM = getPackageManager();

		lvList = (ListView) findViewById(R.id.lv_list);
		ivLine = (ImageView) findViewById(R.id.iv_line);
		ivIcon = (ImageView) findViewById(R.id.iv_icon);
		tvName = (TextView) findViewById(R.id.tv_name);
		tvCacheSize = (TextView) findViewById(R.id.tv_size);
		pbProgress = (ProgressBar) findViewById(R.id.pb_progress);
		llProgress = (LinearLayout) findViewById(R.id.ll_progress);
		llResult = (LinearLayout) findViewById(R.id.ll_result);
		tvResult = (TextView) findViewById(R.id.tv_result);
		btnCleanAll = (Button) findViewById(R.id.btn_clean_all);

		findViewById(R.id.btn_scan).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startScan();
			}
		});

		//startScan();
	}

	private void startScan() {
		mTask = new CacheTask();
		mTask.execute();
	}

	@Override
	protected void onStart() {
		super.onStart();

		startScan();
	}

	@Override
	protected void onStop() {
		super.onStop();

		mTask.stop();
	}

	class CacheTask extends AsyncTask<Void, CacheInfo, Void> {

		private int totalCount;//总数
		private int progress;//当前进度

		private boolean isStop = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			mList = new ArrayList<CleanCacheActivity.CacheInfo>();
			mCacheList = new ArrayList<CleanCacheActivity.CacheInfo>();

			//设置ListView数据
			mAdapter = new CacheAdapter();
			lvList.setAdapter(mAdapter);

			//启动扫描动画
			//注意: 相对的父控件移动,而不是自身;RELATIVE_TO_PARENT
			TranslateAnimation anim = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT, 0,
					Animation.RELATIVE_TO_PARENT, 0,
					Animation.RELATIVE_TO_PARENT, 0,
					Animation.RELATIVE_TO_PARENT, 1);
			anim.setDuration(1000);
			anim.setRepeatMode(Animation.REVERSE);
			anim.setRepeatCount(Animation.INFINITE);

			ivLine.startAnimation(anim);

			//显示进度布局,隐藏结果布局
			llProgress.setVisibility(View.VISIBLE);
			llResult.setVisibility(View.GONE);

			//禁用立即清理按钮
			btnCleanAll.setEnabled(false);
		}

		@Override
		protected Void doInBackground(Void... params) {
			//扫描所有已安装app,获取缓存相关信息
			//有些程序虽然卸载了, 但缓存仍然存在, 这种软件也要扫描出来
			List<PackageInfo> installedPackages = mPM
					.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);

			totalCount = installedPackages.size();

			progress = 0;
			for (PackageInfo packageInfo : installedPackages) {
				String packageName = packageInfo.packageName;
				//根据包名获取缓存信息
				try {
					Method method = mPM.getClass().getMethod(
							"getPackageSizeInfo", String.class,
							IPackageStatsObserver.class);

					//参1: 对象 ; 参2:参数
					method.invoke(mPM, packageName, new MyObserver());

				} catch (Exception e) {
					e.printStackTrace();
				}

				progress++;

				SystemClock.sleep(200);

				if (isStop) {
					break;
				}
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(CacheInfo... values) {
			super.onProgressUpdate(values);

			if (isStop) {
				return;
			}

			//刷新listView
			mAdapter.notifyDataSetChanged();
			//listview滑动到底
			lvList.smoothScrollToPosition(mList.size() - 1);

			CacheInfo info = values[0];

			//更新图标
			ivIcon.setImageDrawable(info.icon);
			//更新名称
			tvName.setText(info.name);
			//更新进度
			int percent = progress * 100 / totalCount;
			pbProgress.setProgress(percent);
			//更新缓存大小
			tvCacheSize.setText("缓存大小:"
					+ Formatter.formatFileSize(getApplicationContext(),
							info.cacheSize));
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (isStop) {
				return;
			}

			//listview滑动到第一个位置
			lvList.smoothScrollToPosition(0);

			//停止扫描动画
			ivLine.clearAnimation();

			//展示扫描结果
			//显示结果布局,隐藏进度布局
			llProgress.setVisibility(View.GONE);
			llResult.setVisibility(View.VISIBLE);

			//计算全部缓存大小
			long totalCache = 0;
			for (CacheInfo info : mCacheList) {
				totalCache += info.cacheSize;
			}

			tvResult.setText("共发现"
					+ mCacheList.size()
					+ "处缓存,共"
					+ Formatter.formatFileSize(getApplicationContext(),
							totalCache) + "空间");

			//启用立即清理按钮
			btnCleanAll.setEnabled(true);
		}

		//更新进度, 装饰模式
		public void updateProgress(CacheInfo info) {
			publishProgress(info);
		}

		public void stop() {
			isStop = true;
		}

	}

	// <uses-permission android:name="android.permission.GET_PACKAGE_SIZE"/>
	class MyObserver extends IPackageStatsObserver.Stub {

		//此方法运行在子线程
		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			//在获取完缓存之后,再初始化对象信息
			CacheInfo info = new CacheInfo();

			info.packageName = pStats.packageName;//当前app的包名
			info.cacheSize = pStats.cacheSize;
			//			info.cacheSize = pStats.cacheSize - 12kb;//缓存大小
			//			
			//			if(info.cacheSize<0) {
			//				info.cacheSize = 0;
			//			}

			//根据包名获取应用信息
			try {
				ApplicationInfo applicationInfo = mPM.getApplicationInfo(
						info.packageName, 0);

				info.name = applicationInfo.loadLabel(mPM).toString();
				info.icon = applicationInfo.loadIcon(mPM);

				//在高版本系统上,系统会至少给每个app预留12KB的缓存, 这个缓存是无法清理掉的
				if (info.cacheSize > 0) {
					mList.add(0, info);//添加到第一个位置
					mCacheList.add(info);//添加到缓存集合中
				} else {
					mList.add(info);//给集合添加对象
				}

				//主线程刷新ListView
				//mAdapter.notifyDataSetChanged();
				mTask.updateProgress(info);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}

	}

	class CacheAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public CacheInfo getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(CleanCacheActivity.this,
						R.layout.item_cache, null);

				holder = new ViewHolder();
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.tv_name);
				holder.tvCacheSize = (TextView) convertView
						.findViewById(R.id.tv_cache_size);
				holder.ivClean = (ImageView) convertView
						.findViewById(R.id.iv_clean);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final CacheInfo info = getItem(position);
			holder.ivIcon.setImageDrawable(info.icon);
			holder.tvName.setText(info.name);
			holder.tvCacheSize.setText("缓存大小:"
					+ Formatter.formatFileSize(getApplicationContext(),
							info.cacheSize));

			if (info.cacheSize > 0) {
				//有缓存,显示清理按钮
				holder.ivClean.setVisibility(View.VISIBLE);
				holder.ivClean.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						//清理单个文件缓存
						//跳到应用信息页, 隐式意图
						Intent infoIntent = new Intent();
						infoIntent
								.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
						infoIntent.setData(Uri.parse("package:"
								+ info.packageName));
						startActivity(infoIntent);
					}
				});
			} else {
				holder.ivClean.setVisibility(View.GONE);
			}

			return convertView;
		}

	}

	static class ViewHolder {
		public ImageView ivIcon;
		public ImageView ivClean;
		public TextView tvName;
		public TextView tvCacheSize;
	}

	class CacheInfo {

		public String name;
		public Drawable icon;
		public String packageName;
		public long cacheSize;

	}

	//立即清理全部缓存
	// <uses-permission android:name="android.permission.CLEAR_APP_CACHE" />
	public void cleanAll(View view) {
		//freeStorageAndNotify: 通过删除缓存来释放空间并通知
		//LRU: least recenttly used,最近最少使用算法
		//A,B,C,D,A,C,E--->B
		//思路: 向系统索要足够大的空间, 那么系统就会删除全部缓存文件来凑空间. 从而间接达到清理缓存目的!
		//Long.MAX_VALUE=8kPB
		try {
			Method method = mPM.getClass().getMethod("freeStorageAndNotify",
					long.class, IPackageDataObserver.class);
			method.invoke(mPM, Long.MAX_VALUE, new IPackageDataObserver.Stub() {

				//子线程运行
				@Override
				public void onRemoveCompleted(String packageName,
						boolean succeeded) throws RemoteException {
					//释放空间完成
					System.out.println("succeeded:" + succeeded);

					//重新自动扫描一遍
					//注意: 一般需要在主线程启动异步任务
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							startScan();
							ToastUtils.showToast(getApplicationContext(),
									"清理完成!");
						}
					});
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
