package cn.itcast.zhxa09.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NoScrollViewPager extends ViewPager {

	public NoScrollViewPager(Context context) {
		super(context);
	}

	public NoScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 
	 * dispatchTouchEvent-->onInterceptTouchEvent-->onTouchEvent
	 * 
	 * 1、down事件  NoScrollViewPager没有拦截  传递给子控件ViewPager
	 * 2、前几个move事件，NoScrollViewPager没有拦截，传递子控件ViewPager，所以此时子控件的ViewPager能够滑动一小段距离
	 * 3、在某一个move事件中，NoScrollViewPager进行拦截，不传递给子控件ViewPager
	 * 
	 * 解决方案：不管是什么事件，全部不拦截
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		/*int action = ev.getAction();
		boolean intercept = super.onInterceptTouchEvent(ev);
		System.out.println("NoScrollViewPager.onInterceptTouchEvent.action="
				+ action + ",intercept=" + intercept);*/
		return false;
	}

	// 父类
	// 父控件
	// ViewPager之所以能够左右滑动是因为ViewPager里面的onTouchEvent代码逻辑来实现
	// 只需要不调用ViewPager中的onTouchEvent，ViewPager就不能左右滑动
	// true:消费了事件
	// false:不消费事件
	// down---move---up
	// 如果一个控件想处理滑动相关的逻辑，必须return true
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		return true;
	}

	// @Override
	// public void setOffscreenPageLimit(int limit) {
	// mOffscreenPageLimit = 0;
	// }

}
