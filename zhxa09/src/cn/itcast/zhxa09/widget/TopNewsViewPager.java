package cn.itcast.zhxa09.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class TopNewsViewPager extends ViewPager {

	private float startX;
	private float startY;

	public TopNewsViewPager(Context context) {
		super(context);
	}

	public TopNewsViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev);
	}

	/**
	 * 1、在down事件中，请求所有的父控件别拦截事件
	 * 2、在move事件中，如果当前的ViewPager处于第0页，此时用户继续向右滑动，请求父控件拦截事件
	 * 3、在move事件中，如果当前的ViewPager处于最后一页，此时用户继续向左滑动，请求父控件拦截事件
	 * 
	 * 区分是否是上下滑动还是左右滑动
	 * 上下滑动：请求父控件拦截
	 * 左右滑动：请求父控件不要拦截
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		// System.out.println("TopNewsViewPager.dispatchTouchEvent.action=" +
		// action);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			getParent().requestDisallowInterceptTouchEvent(true);// 请求父控件别拦截接下来的move事件
			startX = ev.getX();
			startY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			int currentItem = getCurrentItem();
			float moveX = ev.getX();
			float dx = moveX - startX;
			
			float moveY = ev.getY();
			float dy = moveY - startY;
			
			if(Math.abs(dx) > Math.abs(dy)) {
				//左右滑动
				
				if (currentItem == 0) {
					if (dx > 0) {
						// 向右滑动
						getParent().requestDisallowInterceptTouchEvent(false);
					}
				} else if (currentItem == getAdapter().getCount() - 1) {
					if (dx < 0) {
						// 向左滑动
						getParent().requestDisallowInterceptTouchEvent(false);
					}
				}
				
			} else {
				//上下滑动
				getParent().requestDisallowInterceptTouchEvent(false);
			}
			
			break;

		}

		return super.dispatchTouchEvent(ev);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		boolean flag = super.onTouchEvent(ev);
		//System.out.println("action=" + action + ",flag=" + flag);
		return flag;
	}
}
