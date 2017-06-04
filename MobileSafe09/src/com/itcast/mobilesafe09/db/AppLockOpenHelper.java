package com.itcast.mobilesafe09.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 程序锁数据库
 */
public class AppLockOpenHelper extends SQLiteOpenHelper {

	public AppLockOpenHelper(Context context) {
		super(context, "applock.db", null, 1);
	}

	//数据库创建
	@Override
	public void onCreate(SQLiteDatabase db) {
		//记录已加锁的包名
		String sql = "create table applock(_id integer primary key autoincrement, package varchar(50))";
		db.execSQL(sql);
	}

	//数据库更新
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
