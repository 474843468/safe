package cn.itcast.zhxa09.fragment.pager.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import cn.itcast.zhxa09.fragment.pager.BasePager;
/**
 * 首页
 * @author zhengping
 *
 */
public class HomePager extends BasePager {

	public HomePager(Activity activity) {
		super(activity);
	}

	// 动态的更改空的帧布局中的内容
	@Override
	public void initData() {
		System.out.println("首页初始化数据。。。");
		TextView tv = new TextView(mActivity);
		tv.setText("首页");
		tv.setTextColor(Color.RED);
		tv.setTextSize(20);
		tv.setGravity(Gravity.CENTER);

		flContent.addView(tv);
		
		tvTitle.setText("智慧西安");
		ivMenu.setVisibility(View.GONE);
		
		

	}

}
