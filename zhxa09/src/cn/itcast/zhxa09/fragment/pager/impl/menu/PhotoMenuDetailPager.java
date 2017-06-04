package cn.itcast.zhxa09.fragment.pager.impl.menu;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.itcast.zhxa09.R;
import cn.itcast.zhxa09.bean.PhotoBean;
import cn.itcast.zhxa09.bean.PhotoBean.PhotoItemData;
import cn.itcast.zhxa09.fragment.pager.BaseMenuDetailPager;
import cn.itcast.zhxa09.global.GlobalConstants;
import cn.itcast.zhxa09.utils.MyBitmapUtils;

/**
 * 组图菜单详情页
 * 
 * @author zhengping
 * 
 */
public class PhotoMenuDetailPager extends BaseMenuDetailPager implements OnClickListener {

	private ListView lv;
	private GridView gv;
	private ArrayList<PhotoItemData> news;
	private ImageView ivDisplay;

	public PhotoMenuDetailPager(Activity activity, ImageView ivDisplay) {
		super(activity);
		this.ivDisplay = ivDisplay;
		ivDisplay.setOnClickListener(this);
	}

	@Override
	public View initView() {
		/*
		 * TextView tv = new TextView(mActivity); tv.setText("组图菜单详情页");
		 */

		View view = View.inflate(mActivity, R.layout.pager_menu_photo_detail,
				null);
		lv = (ListView) view.findViewById(R.id.lv);
		gv = (GridView) view.findViewById(R.id.gv);
		return view;
	}

	@Override
	public void initData() {
		getDataFromServer();
	}

	private void getDataFromServer() {
		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.GET, GlobalConstants.URL_PHOTOS,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String json = responseInfo.result;
						parseJson(json);
					}

					@Override
					public void onFailure(HttpException error, String msg) {

					}
				});

	}

	protected void parseJson(String json) {
		Gson gson = new Gson();
		PhotoBean photoBean = gson.fromJson(json, PhotoBean.class);
		news = photoBean.data.news;
		
		lv.setAdapter(new MyAdapter());
		gv.setAdapter(new MyAdapter());

	}

	class MyAdapter extends BaseAdapter {

		private MyBitmapUtils bitmapUtils;

		public MyAdapter() {
			bitmapUtils = new MyBitmapUtils(mActivity);
		}

		@Override
		public int getCount() {
			return news.size();
		}

		@Override
		public Object getItem(int position) {
			return news.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		//1、ListView的优化
		//2、ListView显示多种布局类型
		//3、ListView数据错乱的问题解决
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(mActivity,
						R.layout.layout_photo_item, null);
				holder = new ViewHolder();
				holder.tvTitle = (TextView) convertView
						.findViewById(R.id.tvTitle);
				holder.iv = (ImageView) convertView.findViewById(R.id.iv);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.tvTitle.setText(news.get(position).title);

			//1、下载图片
			//2、把图片变成bitmap对象
			//3、把bitmap对象设置给ImageView
			//4、缓存：内存缓存、本地缓存、网络缓存    -->三级缓存
			//5、OOM   --> OOM这个问题基本上是加载图片的时候会引起   内存泄露
			holder.iv.setTag(GlobalConstants.URL_PREFIX + news.get(position).listimage);
			bitmapUtils.display(holder.iv,
					GlobalConstants.URL_PREFIX + news.get(position).listimage);

			return convertView;
		}

	}

	static class ViewHolder {
		ImageView iv;
		TextView tvTitle;
	}

	
	private boolean isListView = true;
	@Override
	public void onClick(View v) {
		
		if(isListView) {
			gv.setVisibility(View.VISIBLE);
			lv.setVisibility(View.GONE);
			isListView = false;
			ivDisplay.setImageResource(R.drawable.icon_pic_list_type);
		} else {
			gv.setVisibility(View.GONE);
			lv.setVisibility(View.VISIBLE);
			isListView = true;
			ivDisplay.setImageResource(R.drawable.icon_pic_grid_type);
		}
		
	}

}
