package com.itcast.mobilesafe09.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 短信备份/还原 工具类
 * 
 *  <uses-permission android:name="android.permission.READ_SMS" />
    <uses-permission android:name="android.permission.WRITE_SMS" />
 */
public class SmsUtils {

	//短信备份
	public static void smsBackup(Context ctx, File file, OnSmsListener listener)
			throws Exception {
		//1. 读取短信数据库
		//com.android.providers.telephony/database/mmssms.db/sms
		//address:电话号码
		//date:短信时间
		//read:是否已读;0表示未读,1表示已读
		//type:短信类型, 1表示收到短信; 2表示发出短信
		//body:短信内容
		Cursor cursor = ctx.getContentResolver().query(
				Uri.parse("content://sms/"),
				new String[] { "address", "date", "read", "type", "body" },
				null, null, null);

		//获取短信总数
		int totalCount = cursor.getCount();

		//以短信总数为进度最大值, 备份短信条数 = 备份进度
		//dialog.setMax(totalCount);
		listener.onSmsTotal(totalCount);//回调短信总数

		ArrayList<SmsInfo> list = new ArrayList<SmsUtils.SmsInfo>();
		if (cursor != null) {

			int progress = 0;
			while (cursor.moveToNext()) {
				SmsInfo info = new SmsInfo();

				info.address = cursor.getString(cursor
						.getColumnIndex("address"));
				info.date = cursor.getString(cursor.getColumnIndex("date"));
				info.read = cursor.getString(cursor.getColumnIndex("read"));
				info.type = cursor.getString(cursor.getColumnIndex("type"));
				info.body = cursor.getString(cursor.getColumnIndex("body"));

				list.add(info);

				progress++;

				//更新进度
				//dialog.setProgress(progress);
				listener.onSmsProgress(progress);//回调短信进度

				SystemClock.sleep(500);//模拟耗时操作
			}

			cursor.close();
		}

		//System.out.println("短信内容:" + list);

		//2. 将短信序列化(转成字符串)后保存在sdcard中
		//xml, json
		//Gson: google 解析json和生成json的工具类
		Gson gson = new Gson();
		String json = gson.toJson(list);

		//System.out.println("序列化后的json:" + json);
		FileWriter writer = new FileWriter(file);
		writer.write(json);
		writer.flush();
		writer.close();
	}

	//短信还原
	public static void smsRestore(Context ctx, File file, OnSmsListener listener)
			throws Exception {
		//1. 从本地文件中反序列化,拿到对象集合
		//2. 遍历集合, 插入到短信数据库
		Gson gson = new Gson();

		//初始化类型(会抄就行), 遇到集合需要反序列化时这样使用
		Type type = new TypeToken<ArrayList<SmsInfo>>() {
		}.getType();

		//参2: 对象类型
		ArrayList<SmsInfo> list = gson.fromJson(new FileReader(file), type);

		//System.out.println("反序列化:" + list);

		//回传短信总数
		listener.onSmsTotal(list.size());

		ContentResolver resolver = ctx.getContentResolver();

		int progress = 0;
		for (SmsInfo info : list) {
			//判断短信是否已经存在, 如果存在,无须插入
			Cursor cursor = resolver.query(Uri.parse("content://sms/"), null,
					"address=? and type=? and date=? and read=? and body=?",
					new String[] { info.address, info.type, info.date,
							info.read, info.body }, null);

			if (cursor != null) {
				if (cursor.moveToFirst()) {
					//短信存在, 无须插入, 循环继续
					continue;
				}
			}

			ContentValues values = new ContentValues();
			values.put("address", info.address);
			values.put("type", info.type);
			values.put("date", info.date);
			values.put("read", info.read);
			values.put("body", info.body);

			resolver.insert(Uri.parse("content://sms/"), values);

			progress++;

			//回传进度
			listener.onSmsProgress(progress);

			SystemClock.sleep(500);
		}

	}

	//短信对象
	public static class SmsInfo {
		public String address;
		public String date;
		public String read;
		public String type;
		public String body;

		@Override
		public String toString() {
			return "SmsInfo [address=" + address + ", body=" + body + "]";
		}
	}

	//回调接口
	public interface OnSmsListener {

		//获取短信总数的回调
		public void onSmsTotal(int totalCount);

		//获取短信进度的回调
		public void onSmsProgress(int progress);

	}

}
