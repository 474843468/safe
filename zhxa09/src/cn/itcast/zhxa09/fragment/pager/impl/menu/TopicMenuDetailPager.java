package cn.itcast.zhxa09.fragment.pager.impl.menu;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import cn.itcast.zhxa09.fragment.pager.BaseMenuDetailPager;
/**
 * 专题菜单详情页
 * @author zhengping
 *
 */
public class TopicMenuDetailPager extends BaseMenuDetailPager {

	public TopicMenuDetailPager(Activity activity) {
		super(activity);
	}

	@Override
	public View initView() {

		TextView tv = new TextView(mActivity);
		tv.setText("专题菜单详情页");
		return tv;
	
	}

}
