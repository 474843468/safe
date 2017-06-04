package com.itcast.mobilesafe09.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.itcast.mobilesafe09.global.GlobalConstants;
import com.itcast.mobilesafe09.utils.PrefUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.text.TextUtils;

/**
 * 手机定位服务
 */
public class LocationService extends Service {

	private LocationManager mLM;
	private MyListener mListener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mLM = (LocationManager) getSystemService(LOCATION_SERVICE);

		mListener = new MyListener();

		//制定一个标准
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);//获取良好的精确度
		criteria.setCostAllowed(true);//允许移动流量的消耗

		//获取当前最好的位置提供者
		String bestProvider = mLM.getBestProvider(criteria, true);

		System.out.println("当前位置提供者:" + bestProvider);

		mLM.requestLocationUpdates(bestProvider, 0, 0, mListener);
	}

	class MyListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			double longitude = location.getLongitude();
			double latitude = location.getLatitude();

			//将经纬度发送给安全号码
			//			String phone = PrefUtils.getString(getApplicationContext(),
			//					GlobalConstants.PREF_SAFE_PHONE, "");
			//			SmsManager.getDefault().sendTextMessage(phone, null,
			//					"longitude:" + longitude + ";latitude:" + latitude, null,
			//					null);

			//将经纬度转成物理实际地址之后再发送短信
			getLocation(longitude, latitude);

			//销毁服务
			stopSelf();//服务自杀的方法
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mLM.removeUpdates(mListener);
		mListener = null;
	}

	//将经纬度转成物理实际地址
	public void getLocation(final double longitude, final double latitude) {
		// 接口地址：http://lbs.juhe.cn/api/getaddressbylngb
		// 支持格式：JSON/XML
		// 请求方式：GET
		// 请求示例：http://lbs.juhe.cn/api/getaddressbylngb?lngx=116.407431&lngy=39.914492
		// 请求参数：
		// 名称 类型 必填 说明
		// lngx String Y google地图经度 (如：119.9772857)
		// lngy String Y google地图纬度 (如：27.327578)
		// dtype String N 返回数据格式：json或xml,默认json

		//		{
		//			resultcode: "1",
		//			resultinfo: "Successful",
		//			row: {
		//			status: "OK",
		//			result: {
		//			location: {
		//			lng: 116.407431,
		//			lat: 39.914492
		//				},
		//			formatted_address: "北京市东城区东长安街",
		//			business: "天安门,前门,大栅栏",
		//			addressComponent: {
		//			city: "北京市",
		//			direction: "",
		//			distance: "",
		//			district: "东城区",
		//			province: "北京市",
		//			street: "东长安街",
		//			street_number: ""
		//				},
		//						cityCode: 131
		//					}
		//				}
		//			}

		HttpUtils utils = new HttpUtils();
		utils.configTimeout(2000);
		utils.send(HttpMethod.GET,
				"http://lbs.juhe.cn/api/getaddressbylngb?lngx=" + longitude
						+ "&lngy=" + latitude, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String result = responseInfo.result;

						try {
							JSONObject jo = new JSONObject(result);

							JSONObject jo1 = jo.getJSONObject("row");

							JSONObject jo2 = jo1.getJSONObject("result");

							String address = jo2.getString("formatted_address");

							System.out.println("物理地址:" + address);

							//发送短信
							if (!TextUtils.isEmpty(address)) {
								sendSms(address);
							} else {
								sendSms("longitude:" + longitude + ";latitude:"
										+ latitude);
							}
						} catch (JSONException e) {
							e.printStackTrace();
							sendSms("longitude:" + longitude + ";latitude:"
									+ latitude);
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						error.printStackTrace();
						sendSms("longitude:" + longitude + ";latitude:"
								+ latitude);
					}

				});

	}

	//给安全号码发送短信
	private void sendSms(String text) {
		String phone = PrefUtils.getString(getApplicationContext(),
				GlobalConstants.PREF_SAFE_PHONE, "");
		SmsManager.getDefault().sendTextMessage(phone, null, text, null, null);
	}
}
