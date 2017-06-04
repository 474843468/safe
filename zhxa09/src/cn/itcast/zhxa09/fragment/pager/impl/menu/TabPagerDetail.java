package cn.itcast.zhxa09.fragment.pager.impl.menu;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;
import cn.itcast.zhxa09.NewsDetailActivity;
import cn.itcast.zhxa09.R;
import cn.itcast.zhxa09.bean.TabInfo;
import cn.itcast.zhxa09.bean.TabInfo.NewsData;
import cn.itcast.zhxa09.bean.TabInfo.TopNewsData;
import cn.itcast.zhxa09.fragment.pager.BaseMenuDetailPager;
import cn.itcast.zhxa09.global.GlobalConstants;
import cn.itcast.zhxa09.utils.PrefUtils;
import cn.itcast.zhxa09.widget.RefreshListView;
import cn.itcast.zhxa09.widget.RefreshListView.OnRefreshListener;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * BaseMenuDetailPager与TabPagerDetail在业务逻辑上没有啥关系 但是代码结构上是一样
 * 
 * @author zhengping
 * 
 */
public class TabPagerDetail extends BaseMenuDetailPager implements OnItemClickListener {

	@ViewInject(R.id.viewPager)
	private ViewPager viewPager;

	private String mUrl;

	private ArrayList<TopNewsData> topnews;

	@ViewInject(R.id.tvTitle)
	private TextView tvTitle;

	@ViewInject(R.id.indicator)
	private CirclePageIndicator indicator;

	@ViewInject(R.id.lvNews)
	private RefreshListView lvNews;

	private ArrayList<NewsData> normalNews;

	private View headerView;

	private String mMoreUrl;

	private MyListAdapter myListAdapter;
	
	private Handler mHandler = new Handler() {
		
		public void handleMessage(android.os.Message msg) {
			
			int currentItem = viewPager.getCurrentItem();
			int nextItem = currentItem + 1;
			if(nextItem > viewPager.getAdapter().getCount()-1) {
				nextItem = 0;
			}
			viewPager.setCurrentItem(nextItem);
			
			mHandler.sendEmptyMessageDelayed(0, 2000);
			
		};
	};

	public TabPagerDetail(Activity activity, String url) {
		super(activity);

		mUrl = url;
	}

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.pager_tab_detail, null);
		
		headerView = View.inflate(mActivity, R.layout.layout_header_news, null);

		ViewUtils.inject(this, view);
		
		//也需要把头布局进行注入
		ViewUtils.inject(this,headerView);
		
		//不要重复的加载同一个头布局对象
		lvNews.addHeaderView(headerView);//将广告轮播条作为头布局加入到ListView中
		
		// viewPager = (ViewPager) view.findViewById(R.id.viewPager);
		// tvTitle = (TextView) view.findViewById(R.id.tvTitle);

		// indicator = (CirclePageIndicator) view.findViewById(R.id.indicator);
		lvNews.setOnRefreshListener(listener);
		/*lvNews.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				getDataFromServer();
			}

			@Override
			public void onLoadMore() {
				
				if (mMoreUrl == null || "".equals(mMoreUrl)) {
					//代表没有下一页数据
					Toast.makeText(mActivity, "没有数据...", Toast.LENGTH_SHORT).show();
					lvNews.onLoadMoreComplete();
				} else {
					getMoreDateFromServer();
				}
				
				
			}
		});*/

		indicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				tvTitle.setText(topnews.get(position).title);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {

			}

			//指的是ViewPager的滚动状态发生改变的时候
			@Override
			public void onPageScrollStateChanged(int state) {
				if(state == ViewPager.SCROLL_STATE_IDLE) {
					mHandler.sendEmptyMessageDelayed(0, 2000);
				} else {
					mHandler.removeMessages(0);
				}

			}
		});
		indicator.setSnap(true);// true代表直接变化 ，没有中间移动的过程
		return view;
	}

	
	public OnRefreshListener listener = new OnRefreshListener() {
		
		@Override
		public void onRefresh() {
			MobclickAgent.onEvent(mActivity, "refresh_list_view");
			getDataFromServer();
		}
		
		@Override
		public void onLoadMore() {
			if (mMoreUrl == null || "".equals(mMoreUrl)) {
				//代表没有下一页数据
				Toast.makeText(mActivity, "没有数据...", Toast.LENGTH_SHORT).show();
				lvNews.onLoadMoreComplete();
			} else {
				getMoreDateFromServer();
			}
		}
	};
	

	// 加载布局文件

	// 初始化数据
	@Override
	public void initData() {
		getDataFromServer();
	}

	private void getDataFromServer() {
		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.GET, GlobalConstants.URL_PREFIX + mUrl,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						// TODO Auto-generated method stub
						String json = responseInfo.result;
						lvNews.onRefreshComplete(true);
						//System.out.println("json=" + json);
						parseJson(json);
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						//失败的时候也需要将头布局进行隐藏
						lvNews.onRefreshComplete(false);
					}
				});
	}
	
	protected void getMoreDateFromServer() {

		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.GET, GlobalConstants.URL_PREFIX + mMoreUrl,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String moreJson = responseInfo.result;
						parseMoreJson(moreJson);
						
						lvNews.onLoadMoreComplete();
						
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						lvNews.onLoadMoreComplete();
					}
				});
	
	}

	protected void parseJson(String json) {
		// 对json数据进行解析
		Gson gson = new Gson();
		TabInfo tabInfo = gson.fromJson(json, TabInfo.class);

		topnews = tabInfo.data.topnews;

		normalNews = tabInfo.data.news;
		
		mMoreUrl = tabInfo.data.more;

		viewPager.setAdapter(new MyAdapter());
		
		//触发消息
		mHandler.sendEmptyMessageDelayed(0, 2000);
		
//		viewPager.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				int action = event.getAction();
//				System.out.println("action=" + action);
//				switch (action) {
//				case MotionEvent.ACTION_DOWN:
//					mHandler.removeMessages(0);
//					break;
//				case MotionEvent.ACTION_UP:
//					mHandler.sendEmptyMessageDelayed(0, 2000);
//					break;
//				case MotionEvent.ACTION_CANCEL:
//					mHandler.sendEmptyMessageDelayed(0, 2000);
//					break;
//
//				default:
//					break;
//				}
//				return false;
//			}
//		});
		
		indicator.setViewPager(viewPager);
		// indicator.setCurrentItem(0);
		indicator.onPageSelected(0);
		myListAdapter = new MyListAdapter();
		lvNews.setAdapter(myListAdapter);
		
		lvNews.setOnItemClickListener(this);

		tvTitle.setText(topnews.get(0).title);
	}
	
	protected void parseMoreJson(String moreJson) {
		// 对json数据进行解析
		Gson gson = new Gson();
		TabInfo tabInfo = gson.fromJson(moreJson, TabInfo.class);

		normalNews.addAll(tabInfo.data.news);
		mMoreUrl = tabInfo.data.more;

		//刷新ListView
		myListAdapter.notifyDataSetChanged();
	}

	/**
	 * 1、ListView的优化 2、ListView显示多种布局类型 3、ListView中item数据显示错乱
	 * 
	 * @author zhengping
	 * 
	 */
	class MyListAdapter extends BaseAdapter {

		private BitmapUtils bitmapUtils;

		public MyListAdapter() {
			bitmapUtils = new BitmapUtils(mActivity);
		}

		@Override
		public int getCount() {
			return normalNews.size();
		}

		@Override
		public Object getItem(int position) {
			return normalNews.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(mActivity,
						R.layout.layout_news_item, null);
				holder = new ViewHolder();
				holder.ivNews = (ImageView) convertView
						.findViewById(R.id.ivNews);
				holder.tvTitle = (TextView) convertView
						.findViewById(R.id.tvTitle);
				holder.tvDate = (TextView) convertView
						.findViewById(R.id.tvDate);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.tvTitle.setText(normalNews.get(position).title);
			holder.tvDate.setText(normalNews.get(position).pubdate);

			bitmapUtils.display(holder.ivNews, GlobalConstants.URL_PREFIX
					+ normalNews.get(position).listimage);
			
			
			int readId = normalNews.get(position).id;
			
			boolean hasRead = PrefUtils.getBoolean(mActivity, String.valueOf(readId), false);
			if(hasRead) {
				holder.tvTitle.setTextColor(Color.GRAY);
			} else {
				holder.tvTitle.setTextColor(Color.BLACK);
			}

			return convertView;
		}

	}

	static class ViewHolder {
		ImageView ivNews;
		TextView tvTitle;
		TextView tvDate;
	}

	class MyAdapter extends PagerAdapter {

		private BitmapUtils bitmapUtils;

		public MyAdapter() {
			bitmapUtils = new BitmapUtils(mActivity);
			// new BitmapUtils(mActivity,"path"); 设置缓存路径
			bitmapUtils.configDefaultLoadingImage(R.drawable.news_pic_default);// 配置加载中的图片
			// bitmapUtils.config
		}

		@Override
		public int getCount() {
			return topnews.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView iv = new ImageView(mActivity);
			// 1、将图片下载 2、将下载的图片变成bitmap对象 3、将bitmap对象设置给imageView
			// 缓存、OOM
			// iv.setImageResource(resId);
			bitmapUtils
					.display(iv,
							GlobalConstants.URL_PREFIX
									+ topnews.get(position).topimage);
			// iv.setimagebi
			iv.setScaleType(ScaleType.CENTER_CROP);

			container.addView(iv);

			return iv;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//把点击的条目置为已读状态
		//由于增加了头布局，第一个头布局的position=0
		int realPosition = position - lvNews.getHeaderViewsCount();
		NewsData newsData = normalNews.get(realPosition);
		int readId = newsData.id;
		//以id为key  存储在sp中
		PrefUtils.setBoolean(mActivity, String.valueOf(readId), true);
		
		//myListAdapter.notifyDataSetChanged();
		//局部刷新
		TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvTitle.setTextColor(Color.GRAY);
		
		
		//界面的跳转
		Intent it = new Intent();
		it.putExtra("url", newsData.url);
		it.setClass(mActivity, NewsDetailActivity.class);
		mActivity.startActivity(it);
		
		
	}

}
