package com.itcast.mobilesafe09.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 查询是否是病毒
 */
public class VirusDao {

	//md5: 待判断apk文件的md5
	public static boolean isVirus(Context ctx, String md5) {
		//查询数据库
		//参1:数据库文件的本地路径; 参2:游标工厂, 参3:访问模式:只读
		SQLiteDatabase database = SQLiteDatabase.openDatabase(ctx.getFilesDir()
				+ "/antivirus.db", null, SQLiteDatabase.OPEN_READONLY);

		Cursor cursor = database.query("datable", null, "md5=?",
				new String[] { md5 }, null, null, null);

		boolean isVirus = false;

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				//是病毒
				isVirus = true;
			}

			cursor.close();
		}

		database.close();

		return isVirus;
	}
}
