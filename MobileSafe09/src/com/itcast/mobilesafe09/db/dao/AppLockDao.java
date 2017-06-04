package com.itcast.mobilesafe09.db.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.itcast.mobilesafe09.db.AppLockOpenHelper;
import com.itcast.mobilesafe09.global.GlobalConstants;

/**
 * 程序锁增删改查(crud)
 * 
 * 单例设计模式, 观察者, 工厂
 */
public class AppLockDao {

	//3. 声明一个实例引用
	//private static BlackNumberDao sInstance = new BlackNumberDao();//饿汉模式, 没有线程安全问题
	private static AppLockDao sInstance = null;//懒汉,节省内存,考虑线程安全问题

	private AppLockOpenHelper mHelper;

	private Context mContext;

	//1. 私有构造方法
	private AppLockDao(Context ctx) {
		mHelper = new AppLockOpenHelper(ctx);

		mContext = ctx;
	}

	//2. 公开方法,获取实例对象
	//A, B
	public static AppLockDao getInstance(Context ctx) {
		if (sInstance == null) {
			//A, B
			synchronized (AppLockDao.class) {
				//A, B
				if (sInstance == null) {
					sInstance = new AppLockDao(ctx);
				}
			}
		}

		return sInstance;
	}

	//添加已加锁app
	public boolean add(String packageName) {
		SQLiteDatabase database = mHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("package", packageName);

		//返回插入记录的id, 如果插入失败,返回-1
		long insert = database.insert("applock", null, values);

		database.close();

		//通知数据库变化
		//可以发广播
		//数据库通知; 参1: 数据库uri,可以自己定义; 参2: 观察者, null
		//了解内容
		mContext.getContentResolver().notifyChange(
				Uri.parse(GlobalConstants.CONTENT_URI_APPLOCK), null);

		return insert != -1;
	}

	//删除
	public boolean delete(String packageName) {
		SQLiteDatabase database = mHelper.getWritableDatabase();

		//返回受影响的行数,返回0表示删除失败
		int delete = database.delete("applock", "package=?",
				new String[] { packageName });

		database.close();

		//通知数据库变化
		//可以发广播
		//数据库通知; 参1: 数据库uri,可以自己定义; 参2: 观察者, null
		mContext.getContentResolver().notifyChange(
				Uri.parse(GlobalConstants.CONTENT_URI_APPLOCK), null);

		return delete != 0;
	}

	//查询是否已加锁
	public boolean find(String packageName) {
		SQLiteDatabase database = mHelper.getWritableDatabase();

		Cursor cursor = database.query("applock", null, "package =?",
				new String[] { packageName }, null, null, null);

		boolean exist = false;
		if (cursor != null) {
			if (cursor.moveToNext()) {
				exist = true;
			}

			cursor.close();
		}

		database.close();

		return exist;
	}

	//查询所有已加锁app
	public ArrayList<String> findAll() {
		SQLiteDatabase database = mHelper.getWritableDatabase();

		Cursor cursor = database.query("applock", new String[] { "package" },
				null, null, null, null, null);

		ArrayList<String> list = new ArrayList<String>();

		if (cursor != null) {

			while (cursor.moveToNext()) {
				String packageName = cursor.getString(0);
				list.add(packageName);
			}

			cursor.close();
		}

		database.close();

		return list;
	}

}
