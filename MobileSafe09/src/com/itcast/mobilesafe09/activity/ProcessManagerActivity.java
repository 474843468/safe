package com.itcast.mobilesafe09.activity;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;

import com.itcast.mobilesafe09.R;
import com.itcast.mobilesafe09.bean.ProcessInfo;
import com.itcast.mobilesafe09.engine.ProcessInfoProvider;
import com.itcast.mobilesafe09.global.GlobalConstants;
import com.itcast.mobilesafe09.service.AutoKillService;
import com.itcast.mobilesafe09.utils.PrefUtils;
import com.itcast.mobilesafe09.utils.ServiceStatusUtils;
import com.itcast.mobilesafe09.utils.ToastUtils;
import com.itcast.mobilesafe09.view.ProgressView;
import com.itcast.mobilesafe09.view.SettingItemView;

/**
 * 进程管理
 * 
 * 开发流程:
 * 
 * 1. 进程管理布局开发
 * 2. 进程数量信息获取: 正在运行数, 可有进程数
 * 3. 内存信息获取: 可用内存数, 总内存(考虑api兼容问题)
 * 4. 获取正在运行的进程列表
 * 5. 区分系统和用户继承; 用ListView展示列表
 * 6. 关联一个库项目
 * 
 * 		6.1 选中当前项目->Properties->Android->Add
 * 		6.2 库: 除了可以访问库中的所有java文件之外,还可以访问资源文件, 这是jar包做不到的
 * 		6.3 打包: 原来怎么打单独项目, 现在就怎么打, 没有任何区别, 系统会自动将库项目打进来
 * 		6.4 注意: 库项目必须和当前项目在同一个盘符下, c, d
 * 
 * 7. 使用粘性ListView(了解)
 * 
 * 		7.1 布局文件中将ListView改为StickyListHeadersListView
 * 		7.2 代码中findviewbyid及相关引用也改为StickyListHeadersListView
 * 		7.3 原来的adapter实现StickyListHeadersAdapter接口
 * 		7.4 重写getHeaderView, 返回标题布局; 重写getHeaderId返回组的id
 * 
 * 8. ListView条目单选(重点)
 * 
 * 		8.1 在ProcessInfo中增加一个boolean变量isChecked,标记当前对象是否选中
 * 		8.2 在getView方法中,根据此标记isChecked来更新Checkbox
 * 		8.3 禁用Checkbox点击事件, 让ListView条目有机会点击
 * 		8.4 监听ListView条目的点击事件,修改对象的isChecked为取反状态, 并同时局部更新Checkbox状态
 * 
 * 9. 全选&反选
 * 10. 跳过手机卫士的选中
 * 
 * 		10.1 getView中遇到手机卫士,隐藏CheckBox
 * 		10.2 ListView条目点击时,跳过手机卫士
 * 		10.3  全选&反选也要跳过手机卫士
 * 
 * 11. 一键清理&界面更新, 注意并发修改异常
 * 12. 提示用户,杀死多少进程,节省多少空间
 * 13. 更新进程数和内存界面
 * 14. 抽屉效果: SlidingDrawer (掌握)
 * 
 * 				 <SlidingDrawer
		            android:layout_width="match_parent"
		            android:layout_height="match_parent"
		            android:content="@+id/content"
		            android:handle="@+id/handle"
		            android:orientation="horizontal" >
		
		            <ImageView
		                android:id="@id/handle"
		                android:layout_width="wrap_content"
		                android:layout_height="wrap_content"
		                android:src="@drawable/lock" />
		
		            <LinearLayout
		                android:id="@id/content"
		                android:layout_width="match_parent"
		                android:layout_height="match_parent"
		                android:background="@color/red" >
		
		                <TextView
		                    android:layout_width="wrap_content"
		                    android:layout_height="wrap_content"
		                    android:text="我是小抽屉!!!" />
		            </LinearLayout>
		        </SlidingDrawer>
		        
	15.手机卫士抽屉布局开发
	16.抽屉箭头动画 : 渐变动画
	17.抽屉拉起之后,修改箭头图片,停止动画, setOnDrawerOpenListener
	18.抽屉关闭后,修改箭头图片,开始动画, setOnDrawerCloseListener
	19.是否显示系统进程: sp记录状态, 重写ListView的getCount方法返回不同个数
	20.锁屏自动清理: 一旦屏幕关闭, 就会自动清理所有的进程
	
			屏幕关闭: 监听系统广播, 后台启动服务AutoKillService注册和注销广播
	
	21.定时器 Timer(拓展)
 */
public class ProcessManagerActivity extends Activity implements OnClickListener {

	private ProgressView pvProcess;
	private ProgressView pvMemory;

	private StickyListHeadersListView lvList;

	private int mRunningProcessNum;
	private int mTotalProcessNum;
	private long mAvailMemory;
	private long mTotalMemory;
	private long mUsedMemory;

	private ArrayList<ProcessInfo> mUserList;
	private ArrayList<ProcessInfo> mSystemList;

	private ProcessAdapter mAdapter;

	private ImageView ivArrow1;
	private ImageView ivArrow2;

	private SlidingDrawer mDrawer;

	private SettingItemView sivShowSystem;
	private SettingItemView sivAutoClear;

	private boolean isShowSystem;//标记是否显示系统进程

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_manager);

		pvProcess = (ProgressView) findViewById(R.id.pv_process);
		pvMemory = (ProgressView) findViewById(R.id.pv_memory);
		lvList = (StickyListHeadersListView) findViewById(R.id.lv_list);
		ivArrow1 = (ImageView) findViewById(R.id.iv_arrow1);
		ivArrow2 = (ImageView) findViewById(R.id.iv_arrow2);
		mDrawer = (SlidingDrawer) findViewById(R.id.drawer);
		sivShowSystem = (SettingItemView) findViewById(R.id.siv_show_system);
		sivAutoClear = (SettingItemView) findViewById(R.id.siv_auto_clear);
		sivShowSystem.setOnClickListener(this);
		sivAutoClear.setOnClickListener(this);

		pvProcess.setTitle("进程数:");
		pvMemory.setTitle("内存:");

		//正在运行的进程数
		mRunningProcessNum = ProcessInfoProvider.getRunningProcessNum(this);
		//可有进程数量
		mTotalProcessNum = ProcessInfoProvider.getTotalProcessNum(this);

		//获取可用内存
		mAvailMemory = ProcessInfoProvider.getAvailMemory(this);
		//总内存
		mTotalMemory = ProcessInfoProvider.getTotalMemory(this);
		//已用内存
		mUsedMemory = mTotalMemory - mAvailMemory;

		updateProcessInfoView();

		initData();

		lvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//修改条目选中状态
				ProcessInfo info = mAdapter.getItem(position);

				//跳过手机卫士
				if (info.packageName.equals(getPackageName())) {
					return;
				}

				info.isChecked = !info.isChecked;

				//刷新listView, 全局刷新, 浪费性能
				//mAdapter.notifyDataSetChanged();

				//修改checkbox选中状态, 局部刷新
				CheckBox cbCheck = (CheckBox) view.findViewById(R.id.cb_check);
				cbCheck.setChecked(info.isChecked);
			}
		});

		initArrowUpAnim();

		//抽屉打开监听
		mDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

			@Override
			public void onDrawerOpened() {
				//箭头向下; 停止动画
				ivArrow1.setImageResource(R.drawable.drawer_arrow_down);
				ivArrow2.setImageResource(R.drawable.drawer_arrow_down);

				ivArrow1.clearAnimation();
				ivArrow2.clearAnimation();
			}
		});

		//抽屉收起监听
		mDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

			@Override
			public void onDrawerClosed() {
				//箭头向上,启动动画
				ivArrow1.setImageResource(R.drawable.drawer_arrow_up);
				ivArrow2.setImageResource(R.drawable.drawer_arrow_up);

				initArrowUpAnim();
			}
		});

		//是否显示系统进程回显
		isShowSystem = PrefUtils.getBoolean(this,
				GlobalConstants.PREF_SHOW_SYSTEM, true);
		sivShowSystem.setToggleOn(isShowSystem);
	}

	//初始化抽屉箭头向上动画
	private void initArrowUpAnim() {
		//渐变动画
		AlphaAnimation anim1 = new AlphaAnimation(0.3f, 1);
		anim1.setDuration(500);
		anim1.setRepeatMode(Animation.REVERSE);
		anim1.setRepeatCount(Animation.INFINITE);
		ivArrow1.startAnimation(anim1);

		AlphaAnimation anim2 = new AlphaAnimation(1, 0.3f);
		anim2.setDuration(500);
		anim2.setRepeatMode(Animation.REVERSE);
		anim2.setRepeatCount(Animation.INFINITE);
		ivArrow2.startAnimation(anim2);
	}

	private void initData() {
		ArrayList<ProcessInfo> list = ProcessInfoProvider.getProcessList(this);

		//区分用户系统进程
		mUserList = new ArrayList<ProcessInfo>();
		mSystemList = new ArrayList<ProcessInfo>();
		for (ProcessInfo info : list) {
			if (info.isUserProcess) {
				mUserList.add(info);
			} else {
				mSystemList.add(info);
			}
		}

		mAdapter = new ProcessAdapter();
		lvList.setAdapter(mAdapter);
	}

	//更新进程信息相关界面
	private void updateProcessInfoView() {
		//进程数量信息
		pvProcess.setLeftText("正在运行" + mRunningProcessNum + "个");
		pvProcess.setRightText("可有进程数:" + mTotalProcessNum);

		int percent = mRunningProcessNum * 100 / mTotalProcessNum;
		pvProcess.setProgress(percent);

		//内存占用信息
		pvMemory.setLeftText("占用内存:"
				+ Formatter.formatFileSize(this, mUsedMemory));
		pvMemory.setRightText("可用内存:"
				+ Formatter.formatFileSize(this, mAvailMemory));
		int progress = (int) (mUsedMemory * 100 / mTotalMemory);
		pvMemory.setProgress(progress);
	}

	class ProcessAdapter extends BaseAdapter implements
			StickyListHeadersAdapter {

		@Override
		public int getCount() {
			if (isShowSystem) {//显示系统进程
				return mUserList.size() + mSystemList.size();
			} else {
				return mUserList.size();
			}
		}

		@Override
		public ProcessInfo getItem(int position) {
			if (position < mUserList.size()) {
				return mUserList.get(position);
			} else {
				return mSystemList.get(position - mUserList.size());
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
				convertView = View.inflate(ProcessManagerActivity.this,
						R.layout.item_process_info, null);

				holder = new ViewHolder();
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.tv_name);
				holder.tvMemory = (TextView) convertView
						.findViewById(R.id.tv_memory);
				holder.cbCheck = (CheckBox) convertView
						.findViewById(R.id.cb_check);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			ProcessInfo info = getItem(position);

			holder.ivIcon.setImageDrawable(info.icon);
			holder.tvName.setText(info.name);
			holder.tvMemory.setText(Formatter.formatFileSize(
					getApplicationContext(), info.memory));

			//跳过手机卫士
			if (info.packageName.equals(getPackageName())) {
				//隐藏Checkbox
				holder.cbCheck.setVisibility(View.GONE);
			} else {
				//更新CheckBox状态
				holder.cbCheck.setChecked(info.isChecked);
				holder.cbCheck.setVisibility(View.VISIBLE);
			}

			return convertView;
		}

		//返回标题布局(头布局)对象
		@Override
		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {
			TitleHolder tHolder;
			if (convertView == null) {
				convertView = View.inflate(ProcessManagerActivity.this,
						R.layout.item_title, null);

				tHolder = new TitleHolder();
				tHolder.tvTitle = (TextView) convertView
						.findViewById(R.id.tv_title);
				convertView.setTag(tHolder);
			} else {
				tHolder = (TitleHolder) convertView.getTag();
			}

			ProcessInfo info = getItem(position);

			if (info.isUserProcess) {
				tHolder.tvTitle.setText("用户进程(" + mUserList.size() + ")");
			} else {
				tHolder.tvTitle.setText("系统进程(" + mSystemList.size() + ")");
			}

			return convertView;
		}

		//返回标题布局的id
		//会将列表分为不同组, 现在有两组, 用户进程组和系统进程组
		//返回的是组的id
		//用户组id=0; 系统组id=1
		@Override
		public long getHeaderId(int position) {
			ProcessInfo info = getItem(position);
			return info.isUserProcess ? 0 : 1;
		}

	}

	static class ViewHolder {
		public ImageView ivIcon;
		public TextView tvName;
		public TextView tvMemory;
		public CheckBox cbCheck;
	}

	static class TitleHolder {
		public TextView tvTitle;
	}

	//全选
	public void selectAll(View view) {
		//将所有对象的isChecked改为true
		//刷新ListView
		for (ProcessInfo info : mUserList) {
			//跳过手机卫士
			if (info.packageName.equals(getPackageName())) {
				continue;
			}

			info.isChecked = true;
		}

		if (isShowSystem) {
			for (ProcessInfo info : mSystemList) {
				info.isChecked = true;
			}
		}

		mAdapter.notifyDataSetChanged();
	}

	//反选
	public void reverseSelect(View view) {
		//将所有对象的isChecked改为相反状态
		//刷新ListView
		for (ProcessInfo info : mUserList) {
			//跳过手机卫士
			if (info.packageName.equals(getPackageName())) {
				continue;
			}

			info.isChecked = !info.isChecked;
		}

		if (isShowSystem) {
			for (ProcessInfo info : mSystemList) {
				info.isChecked = !info.isChecked;
			}
		}

		mAdapter.notifyDataSetChanged();
	}

	//一键清理
	//android.permission.KILL_BACKGROUND_PROCESSES
	public void clearAll(View view) {
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		//保存待删除对象
		ArrayList<ProcessInfo> killedList = new ArrayList<ProcessInfo>();

		//Caused by: java.util.ConcurrentModificationException: 并发修改异常: 当遍历集合时,修改集合元素
		//遍历集合,查看谁被选中, 然后结束被选中的进程
		for (ProcessInfo info : mUserList) {
			if (info.isChecked) {
				//杀死后台进程
				am.killBackgroundProcesses(info.packageName);
				//mUserList.remove(info);
				killedList.add(info);
			}
		}

		for (ProcessInfo info : mSystemList) {
			if (info.isChecked) {
				//杀死后台进程
				am.killBackgroundProcesses(info.packageName);
				//mSystemList.remove(info);
				killedList.add(info);
			}
		}

		//遍历待删除集合
		long savedMemory = 0;//节省的空间
		for (ProcessInfo info : killedList) {
			if (info.isUserProcess) {
				mUserList.remove(info);
			} else {
				mSystemList.remove(info);
			}

			savedMemory += info.memory;
		}

		mAdapter.notifyDataSetChanged();

		//提示用户,杀死多少进程,节省多少空间
		ToastUtils
				.showToast(this, String.format("帮您杀死了%d个进程,共节省%s空间",
						killedList.size(),
						Formatter.formatFileSize(this, savedMemory)));

		//更新进程数和内存
		mRunningProcessNum -= killedList.size();
		mUsedMemory -= savedMemory;
		mAvailMemory += savedMemory;
		updateProcessInfoView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.siv_show_system:

			if (isShowSystem) {
				//不显示系统进程
				PrefUtils.putBoolean(this, GlobalConstants.PREF_SHOW_SYSTEM,
						false);
				isShowSystem = false;
			} else {
				//显示系统进程
				PrefUtils.putBoolean(this, GlobalConstants.PREF_SHOW_SYSTEM,
						true);
				isShowSystem = true;
			}

			//更新开关
			sivShowSystem.toggle();

			//刷新ListView
			mAdapter.notifyDataSetChanged();

			break;
		case R.id.siv_auto_clear:
			//如果服务没有运行,启动服务;否则关闭服务
			if (!ServiceStatusUtils.isServiceRunning(getApplicationContext(),
					AutoKillService.class)) {
				//启动归属地服务
				startService(new Intent(getApplicationContext(),
						AutoKillService.class));

				sivAutoClear.setToggleOn(true);
			} else {
				//停止服务
				stopService(new Intent(getApplicationContext(),
						AutoKillService.class));

				sivAutoClear.setToggleOn(false);
			}
			break;

		default:
			break;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this,
				AutoKillService.class);
		//更新开关图片
		sivAutoClear.setToggleOn(serviceRunning);
	}

}
