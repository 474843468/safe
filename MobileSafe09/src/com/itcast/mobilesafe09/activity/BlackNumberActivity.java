package com.itcast.mobilesafe09.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.itcast.mobilesafe09.R;
import com.itcast.mobilesafe09.bean.BlackNumberInfo;
import com.itcast.mobilesafe09.db.dao.BlackNumberDao;
import com.itcast.mobilesafe09.utils.ToastUtils;

/**
 * 黑名单管理页面
 * 
 * 开发流程:
 * 1. 布局开发, ImageButton
 * 2. 数据库封装
 * 
 * 		2.1 BlackNumberOpenHelper, 创建数据库
 * 		2.2 BlackNumberDao, crud, 单例设计模式
 * 		2.3  单元测试, 测试数据库效果, 添加100条假数据
 * 
 * 3. 黑名单列表ListView展示, 数据适配器BlackNumberAdapter
 * 4. ListView优化(重点), convertView, ViewHolder
 * 5. 模拟耗时, 显示加载中布局, include标签, 自定义ProgressBar(indeterminateDrawable+rotate动画)
 * 6. 给ListView设置数据为空的布局, setEmptyView
 * 7. ListView分页效果, 能够提高性能, 节省流量
 * 
 * 		7.1 数据库分页查询的封装, limit index,20
 * 		7.2 监听ListView滑动到底的事件, setOnScrollListener;
 * 		7.3 加载下一页数据
 * 
 * 				7.3.1: 确定下一页数据的位置,index = mList.size()
 * 				7.3.2: 集合要追加,而不是覆盖, addAll
 * 				7.3.3: ListView刷新, notifyDataSetChanged, 第一页数据setAdapter,其他页刷新
 * 		7.4 细节优化, 不允许重复加载多次, isLoading
 * 		7.5 判断是否还有更多数据
 * 
 * 			7.5.1 获取数据库中总数据个数, Dao
 * 			7.5.2 当前已经加载的数量和总数量进行比对, 如果不小于总数,说明没有更多数据了
 * 8. 添加黑名单
 * 9. 更新黑名单
 * 10. 删除黑名单
 */
public class BlackNumberActivity extends Activity {

	private ListView lvList;
	private BlackNumberDao mDao;
	private ArrayList<BlackNumberInfo> mList = new ArrayList<BlackNumberInfo>();
	private BlackNumberAdapter mAdapter;

	private LinearLayout llLoading;
	private ImageView ivEmpty;//数据为空的布局

	private boolean isLoading;//标记当前是否正在加载

	private int mCurrentPos;//当前被点击更新的位置

	private static final int REQUEST_CODE_ADD = 1;//添加黑名单
	private static final int REQUEST_CODE_UPDATE = 2;//更新黑名单

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_number);

		lvList = (ListView) findViewById(R.id.lv_list);
		llLoading = (LinearLayout) findViewById(R.id.ll_loading);
		ivEmpty = (ImageView) findViewById(R.id.iv_empty);

		mDao = BlackNumberDao.getInstance(this);

		//ListView滑动监听
		lvList.setOnScrollListener(new OnScrollListener() {

			//滑动状态发生变化
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				//ListView空闲状态
				if (scrollState == SCROLL_STATE_IDLE) {
					//判断是否滑动到底部
					//获取当前屏幕显示的最后一个条目的位置
					int lastVisiblePosition = lvList.getLastVisiblePosition();
					//					System.out.println("lastVisiblePosition:"
					//							+ lastVisiblePosition);

					//如果屏幕显示的最后一个条目位置等于这个集合的最后一个条目位置
					//就认为滑动到了底部
					//只有当前没有加载时, 才允许加载下一页数据
					if (lastVisiblePosition == mList.size() - 1 && !isLoading) {
						System.out.println("到底了...");

						//判断是否还有更多数据
						//当前已经加载的数量和总数量进行比对
						if (mList.size() >= mDao.getTotalCount()) {
							ToastUtils.showToast(getApplicationContext(),
									"没有更多数据了");
							return;
						}

						//加载下一页数据
						initData();
					}

				}
			}

			//ListView滑动监听
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});

		lvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mCurrentPos = position;

				//跳到黑名单更新页面
				Intent intent = new Intent(getApplicationContext(),
						EditBlackNumberActivity.class);
				intent.putExtra("isUpdate", true);//代表当前是更新黑名单

				//传递电话号码和拦截类型
				BlackNumberInfo info = mList.get(position);
				intent.putExtra("number", info.number);
				intent.putExtra("mode", info.mode);

				startActivityForResult(intent, REQUEST_CODE_UPDATE);
			}
		});

		initData();
	}

	//初始化数据
	private void initData() {
		//显示加载中布局
		llLoading.setVisibility(View.VISIBLE);

		isLoading = true;//表示正在加载

		new Thread() {

			@Override
			public void run() {

				//模拟耗时操作
				SystemClock.sleep(1000);

				//查询所有黑名单记录
				//mList = mDao.findAll();

				//分页查询记录
				//0,20,40,60
				//System.out.println("当前集合大小:" + mList.size());
				//下一页起始位置刚好等于当前集合长度
				//mList = mDao.findPart(mList.size());
				//下一页数据要和之前的数据追加在一起, 而不是覆盖
				ArrayList<BlackNumberInfo> moreList = mDao.findPart(mList
						.size());
				//mList + moreList
				//1,2,3,4,5, 6,7,8,9,10
				mList.addAll(moreList);//arraylist相加的方法

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						//给ListView设置数据,必备方法
						//设置完数据之后,会展示到第一条数据上
						//只有第一页设置数据, 设置数据只需要执行一次就可以了, 以后全都基于当前数据刷新
						if (mAdapter == null) {
							System.out.println("第一页数据过来了...");
							mAdapter = new BlackNumberAdapter();
							lvList.setAdapter(mAdapter);
						} else {
							System.out.println("其他页数据过来了...");
							//下一页数据过来时,只需要刷新ListView, 这样可以保证ListView不跳到第一条数据展示
							mAdapter.notifyDataSetChanged();//重新计算条目的数量,重新绘制ListView,但不会影响当前条目显示位置
						}
						//						if (mList.isEmpty()) {
						//							ivEmpty.setVisibility(View.VISIBLE);
						//						} else {
						//							ivEmpty.setVisibility(View.GONE);
						//						}
						//设置ListView数据为空的布局, 一旦数据为空,ListView会自动显示该布局;如果数据不为空,ListView就会隐藏该布局
						lvList.setEmptyView(ivEmpty);
						//隐藏加载中布局
						llLoading.setVisibility(View.GONE);

						isLoading = false;//表示加载结束
					}
				});
			}
		}.start();
	}

	//添加黑名单
	public void addBlackNumber(View view) {
		startActivityForResult(new Intent(this, EditBlackNumberActivity.class),
				REQUEST_CODE_ADD);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQUEST_CODE_ADD:
			//添加黑名单
			if (resultCode == RESULT_OK) {
				//保存成功
				//数据回显
				String number = data.getStringExtra("number");
				int mode = data.getIntExtra("mode", 1);

				//给集合添加对象
				BlackNumberInfo info = new BlackNumberInfo();
				info.number = number;
				info.mode = mode;

				//添加到集合的第一个位置
				mList.add(0, info);

				//数据变化了, 此时需要刷新ListView才能生效
				mAdapter.notifyDataSetChanged();
			}

			break;
		case REQUEST_CODE_UPDATE:
			//更新黑名单
			if (resultCode == RESULT_OK) {
				//数据回显
				//String number = data.getStringExtra("number");
				int mode = data.getIntExtra("mode", 1);

				//集合数量不变,但待更新的条目内容会发生变化
				//找到待更新的对象->找到要更新条目的位置->setOnItemClickListener中会回传位置
				BlackNumberInfo info = mList.get(mCurrentPos);
				info.mode = mode;

				//刷新ListView
				mAdapter.notifyDataSetChanged();
			}

			break;

		default:
			break;
		}
	}

	class BlackNumberAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public BlackNumberInfo getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		/**
		 * 1. convertView重用, 大大减少了初始化布局的次数
		 * 2. 使用ViewHolder减少findviewbyid次数
		 * 3. ViewHolder改为static, 保证虚拟机只加载一次,以后直接使用
		 * 4. 不用单独声明View对象, 直接操作convertView就可以
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//View view = null;
			ViewHolder holder = null;
			if (convertView == null) {
				//1. 初始化布局
				convertView = View.inflate(BlackNumberActivity.this,
						R.layout.item_black_number, null);

				//2. 初始化控件,findviewbyid
				holder = new ViewHolder();
				holder.tvNumber = (TextView) convertView
						.findViewById(R.id.tv_number);
				holder.tvMode = (TextView) convertView
						.findViewById(R.id.tv_mode);
				holder.ivDelete = (ImageView) convertView
						.findViewById(R.id.iv_delete);

				//将holder和当前的布局对象绑定在一起
				convertView.setTag(holder);
				//System.out.println("初始化布局了...");
			} else {
				//重用布局
				//view = convertView;
				holder = (ViewHolder) convertView.getTag();//取出和view绑定的holder对象
				//System.out.println("重用布局了....");
			}

			//3. 根据对象更新控件
			final BlackNumberInfo info = getItem(position);

			holder.tvNumber.setText(info.number);

			switch (info.mode) {
			case 1:
				holder.tvMode.setText("拦截电话");
				break;
			case 2:
				holder.tvMode.setText("拦截短信");
				break;
			case 3:
				holder.tvMode.setText("拦截全部");
				break;

			default:
				break;
			}

			//给删除按钮加点击事件
			holder.ivDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//1. 从数据库中删除黑名单
					//2. 从集合中移除对象
					//3. 刷新ListView
					mDao.delete(info.number);
					mList.remove(info);
					mAdapter.notifyDataSetChanged();

					//bug: 在不滑动ListView的前提下,频繁点击删除按钮, 删除完当前页的数据之后,不会自动加载下一页
					//解决方案: 发现当前页数据较少时,自动加载下一页
					if (mList.size() < 5) {
						//尝试自动加载下一页
						initData();
					}
				}
			});

			//4. 返回布局对象
			return convertView;
		}

	}

	//保存findviewbyid的控件
	static class ViewHolder {
		public TextView tvNumber;
		public TextView tvMode;
		public ImageView ivDelete;
	}

}
