package cn.itcast.zhxa09.fragment.pager.impl.menu;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import cn.itcast.zhxa09.fragment.pager.BaseMenuDetailPager;
/**
 * 互动菜单详情页
 * @author zhengping
 *
 */
public class InteractMenuDetailPager extends BaseMenuDetailPager {

	public InteractMenuDetailPager(Activity activity) {
		super(activity);
	}

	@Override
	public View initView() {
		TextView tv = new TextView(mActivity);
		tv.setText("互动菜单详情页");
		return tv;
	}

}
