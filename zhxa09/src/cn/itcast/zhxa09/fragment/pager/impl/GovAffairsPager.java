package cn.itcast.zhxa09.fragment.pager.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import cn.itcast.zhxa09.fragment.pager.BasePager;
/**
 * 政务
 * @author zhengping
 *
 */
public class GovAffairsPager extends BasePager {

	public GovAffairsPager(Activity activity) {
		super(activity);
	}

	// 动态的更改空的帧布局中的内容
	@Override
	public void initData() {
		System.out.println("政务初始化数据。。。");
		TextView tv = new TextView(mActivity);
		tv.setText("政务");
		tv.setTextColor(Color.RED);
		tv.setTextSize(20);
		tv.setGravity(Gravity.CENTER);

		flContent.addView(tv);
		
		tvTitle.setText("人口管理");
		ivMenu.setVisibility(View.VISIBLE);

	}

}
