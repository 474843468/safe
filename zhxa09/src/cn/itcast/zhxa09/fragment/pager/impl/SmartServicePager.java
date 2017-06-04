package cn.itcast.zhxa09.fragment.pager.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import cn.itcast.zhxa09.fragment.pager.BasePager;
/**
 * 智慧服务
 * @author zhengping
 *
 */
public class SmartServicePager extends BasePager {

	public SmartServicePager(Activity activity) {
		super(activity);
	}

	// 动态的更改空的帧布局中的内容
	@Override
	public void initData() {
		System.out.println("智慧服务初始化数据。。。");
		TextView tv = new TextView(mActivity);
		tv.setText("智慧服务");
		tv.setTextColor(Color.RED);
		tv.setTextSize(20);
		tv.setGravity(Gravity.CENTER);

		flContent.addView(tv);
		
		tvTitle.setText("生活");
		ivMenu.setVisibility(View.VISIBLE);
	}

}
