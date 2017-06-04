package com.itcast.mobilesafe09.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.itcast.mobilesafe09.R;

/**
 * 流量统计
 * 
 * 注意: 只能用真机演示
 * 
 * 一旦手机重启,所有流量清零
 * 
 * 借助数据库来保存上次流量, 然后和当前流量累加,从而得到总流量
 * 
 */
public class TrafficStatActivity extends Activity {

	private ListView lvList;
	private ArrayList<TrafficInfo> mList;
	private LinearLayout llLoading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic_stat);

		lvList = (ListView) findViewById(R.id.lv_list);
		llLoading = (LinearLayout) findViewById(R.id.ll_loading);

		//		long totalRxBytes = TrafficStats.getTotalRxBytes();//接收总流量; wifi+移动
		//		long totalTxBytes = TrafficStats.getTotalTxBytes();//发送总流量; wifi+移动
		//
		//		long mobileRxBytes = TrafficStats.getMobileRxBytes();//接收总流量; 移动
		//		long mobileTxBytes = TrafficStats.getMobileTxBytes();//发送总流量; 移动
		//
		//		//uid: 当手机安装完一款app之后,会给这个app分配一个uid来标示
		//		long uidRxBytes = TrafficStats.getUidRxBytes(10098);//微信接收流量
		//		long uidTxBytes = TrafficStats.getUidTxBytes(10098);//微信发送流量
		//
		//		System.out.println("totalRxBytes:" + totalRxBytes);
		//		System.out.println("totalTxBytes:" + totalTxBytes);
		//		System.out.println("mobileRxBytes:" + mobileRxBytes);
		//		System.out.println("mobileTxBytes:" + mobileTxBytes);
		//		System.out.println("uidRxBytes:" + uidRxBytes);
		//		System.out.println("uidTxBytes:" + uidTxBytes);

		initData();
	}

	private void initData() {
		new TrafficTask().execute();
	}

	class TrafficTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			llLoading.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			//遍历已安装的app,获取每个app的流量信息
			PackageManager pm = getPackageManager();
			List<PackageInfo> installedPackages = pm.getInstalledPackages(0);

			mList = new ArrayList<TrafficStatActivity.TrafficInfo>();
			for (PackageInfo packageInfo : installedPackages) {
				TrafficInfo info = new TrafficInfo();

				info.packageName = packageInfo.packageName;

				ApplicationInfo applicationInfo = packageInfo.applicationInfo;
				info.name = applicationInfo.loadLabel(pm).toString();
				info.icon = applicationInfo.loadIcon(pm);

				int uid = applicationInfo.uid;
				info.rev = TrafficStats.getUidRxBytes(uid);
				info.snd = TrafficStats.getUidTxBytes(uid);

				//过滤掉不消耗流量的app
				if (info.rev > 0 || info.snd > 0) {
					mList.add(info);
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			//设置数据
			lvList.setAdapter(new TrafficAdapter());

			llLoading.setVisibility(View.GONE);
		}

	}

	class TrafficInfo {
		public String packageName;
		public String name;
		public Drawable icon;

		public long rev;//接收流量
		public long snd;//发送流量
	}

	class TrafficAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public TrafficInfo getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(TrafficStatActivity.this,
						R.layout.item_traffic_info, null);

				holder = new ViewHolder();
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.tv_name);
				holder.tvRev = (TextView) convertView.findViewById(R.id.tv_rev);
				holder.tvSnd = (TextView) convertView.findViewById(R.id.tv_snd);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			TrafficInfo info = getItem(position);

			holder.ivIcon.setImageDrawable(info.icon);
			holder.tvName.setText(info.name);
			holder.tvRev.setText("接收流量:"
					+ Formatter.formatFileSize(getApplicationContext(),
							info.rev));
			holder.tvSnd.setText("发送流量:"
					+ Formatter.formatFileSize(getApplicationContext(),
							info.snd));

			return convertView;
		}

	}

	static class ViewHolder {
		public ImageView ivIcon;
		public TextView tvName;
		public TextView tvRev;
		public TextView tvSnd;
	}

}
