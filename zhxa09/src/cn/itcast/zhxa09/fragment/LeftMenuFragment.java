package cn.itcast.zhxa09.fragment;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import cn.itcast.zhxa09.MainActivity;
import cn.itcast.zhxa09.R;
import cn.itcast.zhxa09.bean.NewsData;
import cn.itcast.zhxa09.fragment.pager.impl.NewsCenterPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LeftMenuFragment extends BaseFragment implements OnItemClickListener {

	private ListView listView;

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.fragment_left_menu, null);
		listView = (ListView) view.findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		return view;
	}
	
	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mData.data.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		//ListView的优化  
		//1、复用convertView：减少加载布局文件的次数
		//2、使用viewHolder：：减少findViewById的次数
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null) {
				convertView = View.inflate(mActivity, R.layout.layout_left_item, null);//1、加载布局文件
				holder = new ViewHolder();
				holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);//2、初始化控件
				convertView.setTag(holder);//3、存储holder
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.tvContent.setText(mData.data.get(position).title);//4、刷新控件数据
			
			
			if(mClickPosition == position) {
				holder.tvContent.setSelected(true);
				//holder.tvContent.setEnabled(true);
			} else {
				holder.tvContent.setSelected(false);
			}
			
			return convertView;
		}
		
	}
	
	//非静态内部类  会默认拥有外部类的实例对象
	 static class ViewHolder {
		TextView tvContent;
	}

	private NewsData mData;
	public void setData(NewsData newsData) {
		mData = newsData;
		myAdapter = new MyAdapter();
		listView.setAdapter(myAdapter);
		
		mClickPosition = 0;
	}
	
	private int mClickPosition = 0;//ListView的item点击的条目位置
	private MyAdapter myAdapter;
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mClickPosition = position;
		//刷新ListView控件的数据
		//listView.getAdapter().no
		myAdapter.notifyDataSetChanged();
		
		
		//更新NewsCenterPager中flContent中的内容
		setNewsCenterPagerDetail(position);
		
		
		
	}
	private void setNewsCenterPagerDetail(int position) {
		//获取NewsCenterPager的实例对象
		
		//拿到ContentFragment
		MainActivity mainActivity = (MainActivity) mActivity;
		ContentFragment contentFragment = mainActivity.getContentFragment();
		
		NewsCenterPager newsCenterPager = contentFragment.getNewsCenterPager();
		
		//通知newsCenterPager它更改flContent中的内容
		newsCenterPager.setCurrentDetailContent(position);
		
		
		SlidingMenu slidingMenu = mainActivity.getSlidingMenu();
		slidingMenu.toggle();
		
		
		
	}

}
