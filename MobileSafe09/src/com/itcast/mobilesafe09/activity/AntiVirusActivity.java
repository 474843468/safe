package com.itcast.mobilesafe09.activity;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.itcast.mobilesafe09.R;
import com.itcast.mobilesafe09.db.dao.VirusDao;
import com.itcast.mobilesafe09.utils.MD5Utils;

/**
 * 手机杀毒
 * 
 * 手机病毒
 * 
 * 手机杀毒原理: 病毒数据库,扫描已安装的所有app, 然后和病毒数据库比对, 如果发现匹配,就认为是病毒
 * 
 * MD5(apk文件), 用md5进行比对
 * 
 * 开发流程:
 * 1. 病毒数据库封装, VirusDao, 判断是否是病毒
 * 2. 异步任务,开始扫描, 判断每一个apk是否是病毒
 * 3. 使用ListView展示已扫描的对象, 普通做法,没有动态刷新, 一次性加载完成
 * 4. 实现ListView动态刷新,每扫描一个对象,刷新一下ListView
 * 5. 圆形进度条, CircleProgress, 布局配置, 自定义属性
 * 6. 更新圆形进度条进度
 * 7. 更新包名
 * 8. 扫描结果的展示, 根据是否存在病毒提示用户; 控制进度布局和结果布局的显示和隐藏
 * 9. 假冒病毒: 安装两个假冒的病毒apk,同时更新病毒数据库, 注意卸载之后重新运行,保证病毒数据库重新拷贝; 发现病毒后添加在集合的第一个位置
 * 10. 开门动画
 * 
 * 		10.1 调整布局, 两个ImageView拼凑布局
 * 		10.2 获取进度布局缓存的bitmap对象
 * 		10.3 将bitmap分为左半边和右半边
 * 		10.4 将左右两半边图片设置给两个ImageView
 * 		10.5通过属性动画,控制ImageView移动和渐变
 * 
 * 11. 重新扫描, 关门动画
 * 12. 重新扫描按钮被重复点击的问题
 * 
 * 		首先禁用按钮->开门动画结束之后启用按钮->开始关门时禁用按钮
 * 13. 停止异步任务, 加isStop标记
 * 14. 横竖屏切换-> 重新onCreate走一遍
 * 
 * 		14.1 强制竖屏(使用场景广泛)
 * 
 * 				  <activity
            		android:screenOrientation="portrait" >//竖屏
            		android:screenOrientation="landscape" >//横屏
        		  </activity>
        		  
        14.1 不强制竖屏, 能够横屏,但不会重新走onCreate方法
        
        		  <activity
            			android:configChanges="screenSize|keyboardHidden|orientation"
             		>
        			</activity>
        			
    15. 卸载病毒逻辑    			
 */
public class AntiVirusActivity extends Activity {

	private ListView lvList;

	private ArrayList<ScanInfo> mList;//扫描对象集合
	private ArrayList<ScanInfo> mVirusList;//病毒集合

	private VirusAdapter mAdapter;

	private ArcProgress mArcProgress;

	private TextView tvPackage;

	private LinearLayout llProgress;//进度布局
	private LinearLayout llResult;//扫描结果布局
	private LinearLayout llAnim;//开门动画布局

	private TextView tvResult;//扫描结果

	private ImageView ivLeft;
	private ImageView ivRight;

	private Bitmap mLeftBitmap;
	private Bitmap mRightBitmap;

	private Button btnRescan;

	private VirusTask mTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);

		lvList = (ListView) findViewById(R.id.lv_list);
		mArcProgress = (ArcProgress) findViewById(R.id.arc_progress);
		tvPackage = (TextView) findViewById(R.id.tv_package);
		llProgress = (LinearLayout) findViewById(R.id.ll_progress);
		llResult = (LinearLayout) findViewById(R.id.ll_result);
		llAnim = (LinearLayout) findViewById(R.id.ll_anim);
		tvResult = (TextView) findViewById(R.id.tv_result);
		ivLeft = (ImageView) findViewById(R.id.iv_left);
		ivRight = (ImageView) findViewById(R.id.iv_right);

		btnRescan = (Button) findViewById(R.id.btn_rescan);

		btnRescan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//重新扫描
				startCloseAnim();//启动关门动画
			}
		});

		//startScan();

		lvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ScanInfo info = mList.get(position);

				if (info.isVirus) {
					//跳转到卸载页面进行卸载
					Intent i = new Intent();
					i.setAction(Intent.ACTION_DELETE);//设置我们要执行的卸载动作
					i.setData(Uri.parse("package:" + info.packageName));//设置获取到的URI
					startActivity(i);
				}
			}
		});
	}

	//创建/从后台进入
	@Override
	protected void onStart() {
		super.onStart();

		startScan();
	}

	//销毁/退到后台
	@Override
	protected void onStop() {
		super.onStop();
		//停止异步任务
		mTask.stop();
	}

	//开始扫描
	private void startScan() {
		mTask = new VirusTask();
		mTask.execute();
	}

	class VirusTask extends AsyncTask<Void, ScanInfo, Void> {

		private int totalCount;
		private int progress;//当前进度

		private boolean isStop = false;//标记当前是否需要停止

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//初始化扫描对象集合
			mList = new ArrayList<AntiVirusActivity.ScanInfo>();
			mVirusList = new ArrayList<AntiVirusActivity.ScanInfo>();

			//设置扫描数据
			mAdapter = new VirusAdapter();
			lvList.setAdapter(mAdapter);

			//显示进度布局, 隐藏结果布局, 隐藏动画布局
			llProgress.setVisibility(View.VISIBLE);
			llResult.setVisibility(View.GONE);
			llAnim.setVisibility(View.GONE);

			//禁用重新扫描按钮
			btnRescan.setEnabled(false);
		}

		@Override
		protected Void doInBackground(Void... params) {
			//获取所有已安装app
			PackageManager pm = getPackageManager();
			List<PackageInfo> installedPackages = pm.getInstalledPackages(0);

			totalCount = installedPackages.size();//扫描软件总数

			progress = 0;
			for (PackageInfo packageInfo : installedPackages) {
				ScanInfo info = new ScanInfo();

				info.packageName = packageInfo.packageName;

				ApplicationInfo applicationInfo = packageInfo.applicationInfo;

				info.name = applicationInfo.loadLabel(pm).toString();
				info.icon = applicationInfo.loadIcon(pm);

				String sourceDir = applicationInfo.sourceDir;//apk文件安装路径

				//计算apk文件的md5
				String md5 = MD5Utils.getFileMd5(sourceDir);
				boolean isVirus = VirusDao
						.isVirus(getApplicationContext(), md5);

				if (isVirus) {
					//是病毒
					System.out.println("发现病毒!!!!!!!!!!!!!!!!!!!");
					info.isVirus = true;

					mVirusList.add(info);//给病毒集合添加对象
					mList.add(0, info);//将病毒对象添加在集合第一个位置
				} else {
					//不是病毒
					System.out.println("安全");

					info.isVirus = false;

					mList.add(info);
				}

				progress++;//更新进度

				//刷新ListView
				publishProgress(info);//此方法就会导致onProgressUpdate回调

				//The content of the adapter has changed but ListView did not receive a notification. 
				//Make sure the content of your adapter is not modified from a background thread, 
				//but only from the UI thread. 
				//偶发崩溃: 两次ListView刷新间隔时间太短,上次还没有刷新完成, 这次就修改了数据, 导致上次刷新失败,然后崩溃!
				//控制在300毫秒左右
				SystemClock.sleep(200);//延时一段时间, 让刷新界面比较有节奏

				if (isStop) {
					//需要停止
					break;
				}
			}

			return null;
		}

		//更新进度
		@Override
		protected void onProgressUpdate(ScanInfo... values) {
			super.onProgressUpdate(values);

			if (isStop) {
				//需要停止
				return;
			}

			//刷新ListView
			mAdapter.notifyDataSetChanged();

			//让ListView滑动到底部
			//lvList.setSelection(mList.size() - 1);//指定当前item的位置
			lvList.smoothScrollToPosition(mList.size() - 1);//平滑滑动到某个位置

			//更新圆形进度条进度
			int percent = progress * 100 / totalCount;
			mArcProgress.setProgress(percent);

			//更新包名
			tvPackage.setText(values[0].packageName);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (isStop) {
				//需要停止
				return;
			}

			//扫描结束, 滑动到第一个位置
			lvList.smoothScrollToPosition(0);

			//隐藏进度布局, 显示结果布局, 显示动画布局
			llProgress.setVisibility(View.GONE);
			llResult.setVisibility(View.VISIBLE);
			llAnim.setVisibility(View.VISIBLE);

			//显示结果
			if (mVirusList.isEmpty()) {
				tvResult.setText("您的手机很安全");
			} else {
				tvResult.setText("您的手机很危险!");
			}

			//执行开门动画
			//获取当前进度布局展示的结果的图片
			llProgress.setDrawingCacheEnabled(true);//开启图片缓存
			llProgress.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);//高质量缓存
			Bitmap srcBitmap = llProgress.getDrawingCache();//获取当前布局的截图

			//获取左侧图片
			mLeftBitmap = getLeftBitmap(srcBitmap);
			ivLeft.setImageBitmap(mLeftBitmap);

			//设置右侧图片
			mRightBitmap = getRightBitmap(srcBitmap);
			ivRight.setImageBitmap(mRightBitmap);

			//ivLeft.setImageBitmap(srcBitmap);
			//ivRight.setImageBitmap(srcBitmap);

			//开启开门动画
			startOpenAnim();
		}

		//停止异步任务
		public void stop() {
			isStop = true;
		}

	}

	class VirusAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public ScanInfo getItem(int position) {
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
				convertView = View.inflate(AntiVirusActivity.this,
						R.layout.item_virus, null);

				holder = new ViewHolder();
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.tv_name);
				holder.tvStatus = (TextView) convertView
						.findViewById(R.id.tv_status);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			ScanInfo info = getItem(position);
			holder.ivIcon.setImageDrawable(info.icon);
			holder.tvName.setText(info.name);

			if (info.isVirus) {
				holder.tvStatus.setText("病毒");
				holder.tvStatus.setTextColor(Color.RED);
			} else {
				holder.tvStatus.setText("安全");
				holder.tvStatus.setTextColor(Color.GREEN);
			}

			return convertView;
		}

	}

	static class ViewHolder {
		public ImageView ivIcon;
		public TextView tvName;
		public TextView tvStatus;
	}

	//扫描对象
	class ScanInfo {
		public String packageName;
		public String name;
		public Drawable icon;
		public boolean isVirus;
	}

	//获取左侧图片
	public Bitmap getLeftBitmap(Bitmap srcBitmap) {
		int width = srcBitmap.getWidth() / 2;
		//创建一个画板, 宽度为原始图一半,高度和配置不变
		Bitmap desBitmap = Bitmap.createBitmap(width, srcBitmap.getHeight(),
				srcBitmap.getConfig());
		//创建一个画布
		Canvas canvas = new Canvas(desBitmap);
		//绘制图片, 由于画板宽度只有原始图的一半,当在画板上绘制原始图时, 原始图左半边就会落在画板上
		canvas.drawBitmap(srcBitmap, new Matrix(), null);

		return desBitmap;
	}

	//获取右侧图片
	public Bitmap getRightBitmap(Bitmap srcBitmap) {
		int width = srcBitmap.getWidth() / 2;
		//创建一个画板, 宽度为原始图一半,高度和配置不变
		Bitmap desBitmap = Bitmap.createBitmap(width, srcBitmap.getHeight(),
				srcBitmap.getConfig());
		//创建一个画布
		Canvas canvas = new Canvas(desBitmap);

		//在绘制时, 原始图片向左移动一个画板的宽度
		Matrix matrix = new Matrix();
		matrix.postTranslate(-width, 0);

		//绘制图片, 由于画板宽度只有原始图的一半,当在画板上绘制原始图时, 原始图向左移动一个画板的宽度后,右半边就会落在画板上
		canvas.drawBitmap(srcBitmap, matrix, null);

		return desBitmap;
	}

	//开启开门动画
	public void startOpenAnim() {
		//1. 左图左移
		//2. 右图右移
		//3. 左图渐变消失
		//4. 右图渐变消失
		//5. 扫描结果渐变显示
		//属性动画
		//ivLeft.setTranslationX(translationX)//水平移动属性
		//		ObjectAnimator anim1 = ObjectAnimator.ofFloat(ivLeft, "translationX",
		//				0, -mLeftBitmap.getWidth());
		//ivLeft.setAlpha(alpha)

		//属性动画集合
		AnimatorSet set = new AnimatorSet();
		set.playTogether(
				ObjectAnimator.ofFloat(ivLeft, "translationX", 0,
						-mLeftBitmap.getWidth()),
				ObjectAnimator.ofFloat(ivRight, "translationX", 0,
						mRightBitmap.getWidth()),
				ObjectAnimator.ofFloat(ivLeft, "alpha", 1, 0),
				ObjectAnimator.ofFloat(ivRight, "alpha", 1, 0),
				ObjectAnimator.ofFloat(llResult, "alpha", 0, 1));

		set.setDuration(2000);

		set.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				//启用重新扫描按钮
				btnRescan.setEnabled(true);
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}
		});

		set.start();
	}

	//关门动画
	protected void startCloseAnim() {
		//1. 左图右移
		//2. 右图左移
		//3. 左图渐变显示
		//4. 右图渐变显示
		//5. 扫描结果渐变消失

		//禁用重新扫描按钮
		btnRescan.setEnabled(false);

		//属性动画集合
		AnimatorSet set = new AnimatorSet();
		set.playTogether(
				ObjectAnimator.ofFloat(ivLeft, "translationX",
						-mLeftBitmap.getWidth(), 0),
				ObjectAnimator.ofFloat(ivRight, "translationX",
						mRightBitmap.getWidth(), 0),
				ObjectAnimator.ofFloat(ivLeft, "alpha", 0, 1),
				ObjectAnimator.ofFloat(ivRight, "alpha", 0, 1),
				ObjectAnimator.ofFloat(llResult, "alpha", 1, 0));

		set.setDuration(2000);

		set.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				//动画结束时,重新扫描
				startScan();
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}
		});

		set.start();
	}

}
