package com.itcast.mobilesafe09.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.itcast.mobilesafe09.R;
import com.itcast.mobilesafe09.db.dao.CommonNumberDao;
import com.itcast.mobilesafe09.db.dao.CommonNumberDao.ChildInfo;
import com.itcast.mobilesafe09.db.dao.CommonNumberDao.GroupInfo;

/**
 * 常用号码查询
 */
public class CommonNumberActivity extends Activity {

	private ExpandableListView elvList;
	private ArrayList<GroupInfo> mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_number);

		elvList = (ExpandableListView) findViewById(R.id.elv_list);

		//获取电话号码数据
		mList = CommonNumberDao.getCommonNumber(this);

		elvList.setAdapter(new CommonNumberAdapter());

		//设置孩子条目的点击监听
		elvList.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				//				ToastUtils.showToast(getApplicationContext(), "第"
				//						+ groupPosition + "组" + ";第" + childPosition + "项");
				ChildInfo info = mList.get(groupPosition).children
						.get(childPosition);

				//跳到拨打电话页面
				Intent intent = new Intent(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:" + info.number));
				startActivity(intent);

				return false;
			}
		});
	}

	class CommonNumberAdapter extends BaseExpandableListAdapter {

		//获取组的个数
		@Override
		public int getGroupCount() {
			//return 8;
			return mList.size();
		}

		//返回某组孩子数量
		@Override
		public int getChildrenCount(int groupPosition) {
			//return groupPosition + 1;
			return mList.get(groupPosition).children.size();
		}

		//getItem: 返回组对象
		@Override
		public GroupInfo getGroup(int groupPosition) {
			return mList.get(groupPosition);
		}

		//getItem: 返回孩子对象
		@Override
		public ChildInfo getChild(int groupPosition, int childPosition) {
			return mList.get(groupPosition).children.get(childPosition);
		}

		//getId, 返回组id
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		//getId, 返回孩子id
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		//默认就可以
		@Override
		public boolean hasStableIds() {
			return false;
		}

		//getView, 返回组布局
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {

			TextView view = new TextView(getApplicationContext());
			view.setTextColor(Color.BLACK);
			view.setTextSize(20);
			//view.setText("       第" + groupPosition + "组");
			view.setText(getGroup(groupPosition).name);
			view.setBackgroundColor(Color.GRAY);//灰色背景
			view.setPadding(10, 10, 10, 10);

			return view;
		}

		//getView, 返回孩子布局
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			TextView view = new TextView(getApplicationContext());
			view.setTextColor(Color.BLACK);
			view.setTextSize(18);
			//view.setText("第" + groupPosition + "组" + ";第" + childPosition + "项");

			ChildInfo info = getChild(groupPosition, childPosition);

			view.setText(info.name + "\n" + info.number);

			view.setPadding(10, 10, 10, 10);

			return view;
		}

		//孩子是否可以点击
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}

}
