package com.itcast.mobilesafe09.db.dao;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 常用号码数据库查询
 */
public class CommonNumberDao {

	public static ArrayList<GroupInfo> getCommonNumber(Context ctx) {
		//查询数据库
		//参1:数据库文件的本地路径; 参2:游标工厂, 参3:访问模式:只读
		SQLiteDatabase database = SQLiteDatabase.openDatabase(ctx.getFilesDir()
				+ "/commonnum.db", null, SQLiteDatabase.OPEN_READONLY);

		Cursor cursor = database.query("classlist", new String[] { "name",
				"idx" }, null, null, null, null, null);

		ArrayList<GroupInfo> list = new ArrayList<CommonNumberDao.GroupInfo>();

		if (cursor != null) {

			while (cursor.moveToNext()) {
				GroupInfo info = new GroupInfo();

				String name = cursor.getString(0);
				String idx = cursor.getString(1);

				info.name = name;
				info.idx = idx;
				//获取组信息的同时, 获取当前组的所有孩子信息
				info.children = getChildList(idx, database);

				list.add(info);
			}

			cursor.close();
		}

		database.close();

		return list;
	}

	//获取某组电话号码列表
	private static ArrayList<ChildInfo> getChildList(String idx,
			SQLiteDatabase database) {
		Cursor cursor = database.query("table" + idx, new String[] { "number",
				"name" }, null, null, null, null, null);

		ArrayList<ChildInfo> list = new ArrayList<CommonNumberDao.ChildInfo>();
		if (cursor != null) {
			while (cursor.moveToNext()) {
				ChildInfo info = new ChildInfo();
				String number = cursor.getString(0);
				String name = cursor.getString(1);

				info.number = number;
				info.name = name;

				list.add(info);
			}

			cursor.close();
		}

		//database.close();//此处不能关闭数据库,保证其他组可以继续查询孩子列表

		return list;
	}

	//组对象
	public static class GroupInfo {
		public String name;
		public String idx;
		public ArrayList<ChildInfo> children;
	}

	//子对象
	public static class ChildInfo {
		public String number;
		public String name;
	}
}
