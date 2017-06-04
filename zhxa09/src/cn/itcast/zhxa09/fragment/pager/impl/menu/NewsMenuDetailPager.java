package cn.itcast.zhxa09.fragment.pager.impl.menu;

import java.util.ArrayList;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.viewpagerindicator.TabPageIndicator;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.itcast.zhxa09.MainActivity;
import cn.itcast.zhxa09.R;
import cn.itcast.zhxa09.bean.NewsData.NewsTabData;
import cn.itcast.zhxa09.fragment.pager.BaseMenuDetailPager;

/**
 * 新闻菜单详情页
 * 
 * @author zhengping
 * 
 */
public class NewsMenuDetailPager extends BaseMenuDetailPager {

	@ViewInject(R.id.viewPager)
	private ViewPager viewPager;
	
	@ViewInject(R.id.indicator)
	private TabPageIndicator indicator;

	private ArrayList<NewsTabData> tabDatas;

	private ArrayList<TabPagerDetail> tabPagers;

	public NewsMenuDetailPager(Activity activity,
			ArrayList<NewsTabData> children) {
		super(activity);

		tabDatas = children;

	}

	@Override
	public View initView() {
		// TextView tv = new TextView(mActivity);
		// tv.setText("新闻菜单详情页");
		View view = View.inflate(mActivity, R.layout.pager_menu_news_detail,
				null);
		ViewUtils.inject(this, view);
		return view;
	}
	/**
	 * setOnClickListener
	 * onClick
	 * xutils
	 * 
	 * onClick的属性只能针对于activity才能使用
	 * @param v
	 */
	@OnClick(R.id.ivNext)
	public void next(View v) {
		//System.out.println("下一页");
		int currentItem = viewPager.getCurrentItem();
		int nextItem = currentItem + 1;
		viewPager.setCurrentItem(nextItem);
	}
	
	/**
	 * 使用ViewPagerIndicator的步骤
	 * 1、关联库
	 * 		v4包冲突的问题两种解决方案：1、删除  2、将版本变为一致
	 * 2、在布局文件中进行页签控件的放置
	 * 3、页签控件初始化
	 * 4、在ViewPager设置数据之后，将ViewPager和页签控件绑定在一起
	 * 5、重写adapter中的getPageTitle的方法
	 * 6、一旦ViewPager和页签控件绑定起来之后，对于界面滑动的事件，应该设置给页签控件
	 */
	@Override
	public void initData() {
		viewPager.setAdapter(new MyAdapter());
		
		indicator.setViewPager(viewPager);//将ViewPager和页签控件绑定在一起
		
		indicator.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				if(position == 0) {
					//SlidingMenu能够全屏触摸打开
					setSlidingMenuEnable(true);
				} else {
					//SlidingMenu不能够全屏触摸打开
					setSlidingMenuEnable(false);
				}
				
				tabPagers.get(position).initData();
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub
				
			}
		});
		
		tabPagers = new ArrayList<TabPagerDetail>();
		
		for(int i=0;i<tabDatas.size();i++) {
			TabPagerDetail tabDetail = new TabPagerDetail(mActivity,tabDatas.get(i).url);
			tabPagers.add(tabDetail);
		}
		tabPagers.get(0).initData();
	}
	
	private void setSlidingMenuEnable(boolean enable) {

		MainActivity mainActivity = (MainActivity) mActivity;
		SlidingMenu slidingMenu = mainActivity.getSlidingMenu();

		if (enable) {
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
	}

	class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return tabDatas.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			TabPagerDetail tabPagerDetail = tabPagers.get(position);
			container.addView(tabPagerDetail.mContentView);
			
			
			
			//TextView tv = new TextView(mActivity);
			//tv.setText(tabDatas.get(position).title);

			//container.addView(tabPagerDetail.mContentView);

			return tabPagerDetail.mContentView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
		
		//这个方法是给页签控件调用的，
		//告诉页签控件应该在哪个位置上显示什么字符串
		@Override
		public CharSequence getPageTitle(int position) {
			return tabDatas.get(position).title;
		}

	}

}
