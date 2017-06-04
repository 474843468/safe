package com.itcast.mobilesafe09.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itcast.mobilesafe09.R;
import com.itcast.mobilesafe09.bean.AppInfo;
import com.itcast.mobilesafe09.db.dao.AppLockDao;
import com.itcast.mobilesafe09.engine.AppInfoProvider;

/**
 * 程序锁管理
 * 
 * 1. 布局开发
 * 2. 点击按钮,切换已加锁和未加锁
 * 3. 程序锁数据库封装, 和黑名单数据库类似
 * 4. 使用ListView展示未加锁和已加锁数据
 * 
 * 		区分已加锁和未加锁: 获取所有已安装的app, 分别从数据库查询判断是否已加锁; 如果已加锁,就放在已加锁集合中;否则放在未加锁集合中
 * 		共用一个adapter, 通过boolean标记是已加锁还是未加锁
 * 
 * 5. 加锁和解锁的点击事件处理
 * 6. 事件传递机制了解; 苹果例子; 父控件先拿到事件,然后从上向下传递; 如果子控件不处理,再从下向上传递
 * 7. 条目移动动画TranslateAnimation; 注意: 在动画结束之后再刷新数据
 * 8. 更新已加锁未加锁数量, 在getCount中调用
 * 9. AsyncTask: 异步任务
 */
public class AppLockActivity extends Activity implements OnClickListener {

	private ListView lvUnlock;
	private ListView lvLock;

	private Button btnUnlock;
	private Button btnLock;

	private AppLockDao mDao;

	private ArrayList<AppInfo> mUnlockList;
	private ArrayList<AppInfo> mLockList;

	private AppLockAdapter mUnlockAdapter;
	private AppLockAdapter mLockAdapter;

	private TextView tvLockNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);

		mDao = AppLockDao.getInstance(this);

		lvUnlock = (ListView) findViewById(R.id.lv_unlock);
		lvLock = (ListView) findViewById(R.id.lv_lock);

		btnUnlock = (Button) findViewById(R.id.btn_unlock);
		btnUnlock.setOnClickListener(this);

		btnLock = (Button) findViewById(R.id.btn_lock);
		btnLock.setOnClickListener(this);

		tvLockNum = (TextView) findViewById(R.id.tv_lock_num);

		initData();
	}

	private void initData() {
		//AsyncTask: 既能执行异步耗时任务,又能主界面更新
		//AsyncTask = Thread + Handler
		//		new Thread() {
		//			@Override
		//			public void run() {
		//				//获取所有已安装app
		//				ArrayList<AppInfo> list = AppInfoProvider
		//						.getInstalledAppList(getApplicationContext());
		//
		//				//区分已加锁和未加锁
		//				mLockList = new ArrayList<AppInfo>();
		//				mUnlockList = new ArrayList<AppInfo>();
		//
		//				for (AppInfo info : list) {
		//					if (mDao.find(info.packageName)) {
		//						//已加锁
		//						mLockList.add(info);
		//					} else {
		//						//未加锁
		//						mUnlockList.add(info);
		//					}
		//				}
		//
		//				runOnUiThread(new Runnable() {
		//
		//					@Override
		//					public void run() {
		//						//设置未加锁数据
		//						mUnlockAdapter = new AppLockAdapter(false);
		//						lvUnlock.setAdapter(mUnlockAdapter);
		//
		//						//设置已加锁数据
		//						mLockAdapter = new AppLockAdapter(true);
		//						lvLock.setAdapter(mLockAdapter);
		//
		//					}
		//				});
		//			}
		//		}.start();

		//启动异步任务
		new AppLockTask().execute(10, "Hello AsyncTask!!!");
	}

	//自定义异步任务
	//高级用法(了解):
	//1. 第一个泛型: 参数类型, 和doInBackground参数类型及execute传参类型一致
	//2. 第二个泛型: 进度数据类型, 和onProgressUpdate参数类型一致
	//3. 第三个泛型: 请求结果类型, 和doInBackground返回值及onPostExecute参数类型一致
	class AppLockTask extends AsyncTask<Object, Integer, String> {

		//预加载, 在后台耗时执行之前的准备工作
		//运行在主线程
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			System.out.println("onPreExecute");
		}

		//在后台运行, 执行耗时操作
		//运行在子线程
		@Override
		protected String doInBackground(Object... params) {
			System.out.println("doInBackground");

			//获取execute传递过来的参数
			Integer p1 = (Integer) params[0];
			String p2 = (String) params[1];

			System.out.println(p1);
			System.out.println(p2);

			//获取所有已安装app
			ArrayList<AppInfo> list = AppInfoProvider
					.getInstalledAppList(getApplicationContext());

			//区分已加锁和未加锁
			mLockList = new ArrayList<AppInfo>();
			mUnlockList = new ArrayList<AppInfo>();

			int progress = 0;
			for (AppInfo info : list) {
				if (mDao.find(info.packageName)) {
					//已加锁
					mLockList.add(info);
				} else {
					//未加锁
					mUnlockList.add(info);
				}

				progress++;

				//通知进度更新, 此方法会回调onProgressUpdate
				publishProgress(progress);
			}

			return "请求结果OK";
		}

		//进度更新
		//运行在主线程
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			System.out.println("当前进度:" + values[0]);
		}

		//后台执行结束之后, 走此方法
		//界面更新
		//运行在主线程
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			System.out.println("onPostExecute:" + result);

			//设置未加锁数据
			mUnlockAdapter = new AppLockAdapter(false);
			lvUnlock.setAdapter(mUnlockAdapter);

			//设置已加锁数据
			mLockAdapter = new AppLockAdapter(true);
			lvLock.setAdapter(mLockAdapter);
		}

	}

	//更新加锁数量
	private void updateLockNum(boolean isLock) {
		if (isLock) {
			tvLockNum.setText("已加锁(" + mLockList.size() + ")");
		} else {
			tvLockNum.setText("未加锁(" + mUnlockList.size() + ")");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_unlock:
			//显示未加锁ListView,隐藏已加锁ListView
			lvUnlock.setVisibility(View.VISIBLE);
			lvLock.setVisibility(View.GONE);

			//更新按钮图片
			btnUnlock.setBackgroundResource(R.drawable.tab_left_pressed);
			btnLock.setBackgroundResource(R.drawable.tab_right_default);

			break;
		case R.id.btn_lock:
			//显示已加锁ListView,隐藏未加锁ListView
			lvUnlock.setVisibility(View.GONE);
			lvLock.setVisibility(View.VISIBLE);

			//更新按钮图片
			btnUnlock.setBackgroundResource(R.drawable.tab_left_default);
			btnLock.setBackgroundResource(R.drawable.tab_right_pressed);
			break;

		default:
			break;
		}
	}

	//未加锁/已加锁数据适配器
	class AppLockAdapter extends BaseAdapter {

		private boolean isLock;//标记是否是已加锁

		private TranslateAnimation animRight;
		private TranslateAnimation animLeft;

		public AppLockAdapter(boolean isLock) {
			this.isLock = isLock;

			//初始化动画
			//右移动画
			animRight = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
					Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF,
					0, Animation.RELATIVE_TO_SELF, 0);
			animRight.setDuration(500);

			//左移动画
			animLeft = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
					Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF,
					0, Animation.RELATIVE_TO_SELF, 0);
			animLeft.setDuration(500);
		}

		@Override
		public int getCount() {
			//当刷新ListView或者给ListView设置adapter时, 系统肯定会调用此方法来计算条目数量
			//在此处可以更新加锁数量
			updateLockNum(isLock);

			if (isLock) {
				return mLockList.size();
			} else {
				return mUnlockList.size();
			}
		}

		@Override
		public AppInfo getItem(int position) {
			if (isLock) {
				return mLockList.get(position);
			} else {
				return mUnlockList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(AppLockActivity.this,
						R.layout.item_applock, null);

				holder = new ViewHolder();
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.tv_name);
				holder.ivLock = (ImageView) convertView
						.findViewById(R.id.iv_lock);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final AppInfo info = getItem(position);
			holder.ivIcon.setImageDrawable(info.icon);
			holder.tvName.setText(info.name);

			//为了在OnClick中启动动画, 可以单独声明一个final的View对象
			final View view = convertView;

			if (isLock) {
				//已加锁
				holder.ivLock.setImageResource(R.drawable.btn_unlock_selector);

				holder.ivLock.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						animLeft.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {

							}

							@Override
							public void onAnimationRepeat(Animation animation) {

							}

							@Override
							public void onAnimationEnd(Animation animation) {
								//解锁
								//1. 从数据库移除
								//2. 从已加锁集合中移除
								//3. 给未加锁集合添加
								//4. 刷新ListView
								mDao.delete(info.packageName);
								mLockList.remove(info);
								mUnlockList.add(info);
								mUnlockAdapter.notifyDataSetChanged();
								mLockAdapter.notifyDataSetChanged();
							}
						});

						view.startAnimation(animLeft);
					}
				});

			} else {
				//未加锁
				holder.ivLock.setImageResource(R.drawable.btn_lock_selector);

				holder.ivLock.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						//必须在动画执行结束之后再刷新数据
						animRight.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {

							}

							@Override
							public void onAnimationRepeat(Animation animation) {

							}

							@Override
							public void onAnimationEnd(Animation animation) {
								//动画结束
								//加锁
								//1. 给数据库添加app包名
								//2. 从未加锁集合中移除
								//3. 给已加锁集合添加
								//4. 刷新ListView
								mDao.add(info.packageName);
								mUnlockList.remove(info);
								mLockList.add(info);
								mUnlockAdapter.notifyDataSetChanged();
								mLockAdapter.notifyDataSetChanged();
							}
						});

						//启动动画, 异步执行,不会阻塞
						view.startAnimation(animRight);
					}
				});
			}

			return convertView;
		}

	}

	static class ViewHolder {
		public ImageView ivIcon;
		public TextView tvName;
		public ImageView ivLock;
	}

}
