package cn.itcast.zhxa09.fragment.pager;

import android.app.Activity;
import android.view.View;

/**
 * 新闻菜单详情页的基类
 * @author zhengping
 *
 */
public abstract class BaseMenuDetailPager {
	
	public Activity mActivity;
	public View mContentView;
	
	public BaseMenuDetailPager(Activity activity) {
		this.mActivity = activity;
		mContentView = initView();
	}
	
	//由于每一个菜单详情界面的布局完全不一样，没有共性，所以这一部分工作必须由子类来完成
	public abstract View initView();
	
	public void initData() {
		
	}

}
