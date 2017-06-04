package cn.itcast.zhxa09.fragment.pager.impl;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import cn.itcast.zhxa09.MainActivity;
import cn.itcast.zhxa09.bean.NewsData;
import cn.itcast.zhxa09.fragment.LeftMenuFragment;
import cn.itcast.zhxa09.fragment.pager.BaseMenuDetailPager;
import cn.itcast.zhxa09.fragment.pager.BasePager;
import cn.itcast.zhxa09.fragment.pager.impl.menu.InteractMenuDetailPager;
import cn.itcast.zhxa09.fragment.pager.impl.menu.NewsMenuDetailPager;
import cn.itcast.zhxa09.fragment.pager.impl.menu.PhotoMenuDetailPager;
import cn.itcast.zhxa09.fragment.pager.impl.menu.TopicMenuDetailPager;
import cn.itcast.zhxa09.global.GlobalConstants;
import cn.itcast.zhxa09.utils.CacheUtils;

/**
 * 新闻中心
 * 
 * @author zhengping
 * 
 */
public class NewsCenterPager extends BasePager {

	private ArrayList<BaseMenuDetailPager> mMenuDetailPagers;
	private NewsData newsData;

	public NewsCenterPager(Activity activity) {
		super(activity);
	}

	// 动态的更改空的帧布局中的内容
	@Override
	public void initData() {
		System.out.println("新闻中心初始化数据。。。");
		TextView tv = new TextView(mActivity);
		tv.setText("新闻中心");
		tv.setTextColor(Color.RED);
		tv.setTextSize(20);
		tv.setGravity(Gravity.CENTER);

		flContent.addView(tv);

		tvTitle.setText("新闻");
		ivMenu.setVisibility(View.VISIBLE);

		String cache = CacheUtils.getCache(mActivity,
				GlobalConstants.URL_CATEGORY);
		if (cache == null) {
			// 没有缓存
		} else {
			// 有缓存
			parseJson(cache);
		}
		// 保证每一次都需要去访问服务器的数据
		getDataFromServer();

	}

	private void getDataFromServer() {
		// 加载服务器的数据
		// URLConnection-->xutils,okhttp,volley HttpClient封装

		HttpUtils utils = new HttpUtils();

		// 异步加载
		utils.send(HttpMethod.GET, GlobalConstants.URL_CATEGORY,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String json = responseInfo.result;
						// System.out.println("json=" + json);
						CacheUtils.setCache(mActivity,
								GlobalConstants.URL_CATEGORY, json);
						parseJson(json);
					}

					@Override
					public void onFailure(HttpException error, String msg) {

					}
				});

	}

	// json：体积小 解析速度快
	// xml
	// 解析json数据
	// 1、使用JSONObject
	// 2、使用开源框架
	// Gson -- Google
	// fastjson -- Alibaba
	private void parseJson(String json) {
		Gson gson = new Gson();
		newsData = gson.fromJson(json, NewsData.class);
		System.out.println("newsData=" + newsData);

		setLeftMenuData(newsData);
		
		
		mMenuDetailPagers = new ArrayList<BaseMenuDetailPager>();
		mMenuDetailPagers.add(new NewsMenuDetailPager(mActivity,newsData.data.get(0).children));
		mMenuDetailPagers.add(new TopicMenuDetailPager(mActivity));
		mMenuDetailPagers.add(new PhotoMenuDetailPager(mActivity,ivDisplay));
		mMenuDetailPagers.add(new InteractMenuDetailPager(mActivity));
		
		setCurrentDetailContent(0);//默认显示新闻菜单详情页中的内容
	}

	private void setLeftMenuData(NewsData newsData) {
		MainActivity mainActivity = (MainActivity) mActivity;
		LeftMenuFragment leftMenuFragment = mainActivity.getLeftMenuFragment();
		leftMenuFragment.setData(newsData);

	}

	public void setCurrentDetailContent(int position) {
		// 改变flContent中的内容
		flContent.removeAllViews();
		BaseMenuDetailPager menuDetailPager = mMenuDetailPagers.get(position);
		menuDetailPager.initData();
		tvTitle.setText(newsData.data.get(position).title);
		flContent.addView(menuDetailPager.mContentView);
		
		/*if(menuDetailPager instanceof PhotoMenuDetailPager) {
			
		} else {
			
		}*/
		if(position == 2) {
			ivDisplay.setVisibility(View.VISIBLE);
		} else {
			ivDisplay.setVisibility(View.GONE);
		}
		
		/*TextView tv = new TextView(mActivity);
		switch (position) {
		case 0:
			tv.setText("新闻菜单详情页");
			flContent.addView(tv);
			break;
		case 1:
			tv.setText("专题菜单详情页");
			flContent.addView(tv);
			break;
		case 2:
			tv.setText("组图菜单详情页");
			flContent.addView(tv);
			break;
		case 3:
			tv.setText("互动菜单详情页");
			flContent.addView(tv);
			break;

		}*/
	}

}
