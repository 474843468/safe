package com.itcast.mobilesafe09.db.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itcast.mobilesafe09.bean.BlackNumberInfo;
import com.itcast.mobilesafe09.db.BlackNumberOpenHelper;

/**
 * 黑名单增删改查(crud)
 * 
 * 单例设计模式, 观察者, 工厂
 */
public class BlackNumberDao {

	//3. 声明一个实例引用
	//private static BlackNumberDao sInstance = new BlackNumberDao();//饿汉模式, 没有线程安全问题
	private static BlackNumberDao sInstance = null;//懒汉,节省内存,考虑线程安全问题

	private BlackNumberOpenHelper mHelper;

	//1. 私有构造方法
	private BlackNumberDao(Context ctx) {
		mHelper = new BlackNumberOpenHelper(ctx);
	}

	//2. 公开方法,获取实例对象
	//A, B
	public static BlackNumberDao getInstance(Context ctx) {
		if (sInstance == null) {
			//A, B
			synchronized (BlackNumberDao.class) {
				//A, B
				if (sInstance == null) {
					sInstance = new BlackNumberDao(ctx);
				}
			}
		}

		return sInstance;
	}

	//添加黑名单
	public boolean add(String number, int mode) {
		SQLiteDatabase database = mHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);

		//返回插入记录的id, 如果插入失败,返回-1
		long insert = database.insert("blacknumber", null, values);

		database.close();

		return insert != -1;
	}

	//删除黑名单
	public boolean delete(String number) {
		SQLiteDatabase database = mHelper.getWritableDatabase();

		//返回受影响的行数,返回0表示删除失败
		int delete = database.delete("blacknumber", "number=?",
				new String[] { number });

		database.close();

		return delete != 0;
	}

	//更新黑名单, 更新拦截模式
	public boolean update(String number, int mode) {
		SQLiteDatabase database = mHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("mode", mode);

		//返回受影响的行数
		int update = database.update("blacknumber", values, "number=?",
				new String[] { number });

		database.close();

		return update != 0;
	}

	//查询黑名单, 查询某个号码是否是黑名单号码
	public boolean find(String number) {
		SQLiteDatabase database = mHelper.getWritableDatabase();

		Cursor cursor = database.query("blacknumber", null, "number =?",
				new String[] { number }, null, null, null);

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

	//查询拦截模式
	public int findMode(String number) {
		SQLiteDatabase database = mHelper.getWritableDatabase();

		Cursor cursor = database.query("blacknumber", new String[] { "mode" },
				"number=?", new String[] { number }, null, null, null);

		int mode = 1;
		if (cursor != null) {
			if (cursor.moveToNext()) {
				mode = cursor.getInt(0);
			}

			cursor.close();
		}

		database.close();

		return mode;
	}

	//查询所有黑名单
	public ArrayList<BlackNumberInfo> findAll() {
		SQLiteDatabase database = mHelper.getWritableDatabase();

		Cursor cursor = database.query("blacknumber", new String[] { "number",
				"mode" }, null, null, null, null, null);

		ArrayList<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();

		if (cursor != null) {

			while (cursor.moveToNext()) {
				BlackNumberInfo info = new BlackNumberInfo();

				String number = cursor.getString(0);
				int mode = cursor.getInt(1);

				info.number = number;
				info.mode = mode;

				list.add(info);
			}

			cursor.close();
		}

		database.close();

		return list;
	}

	//分页查询所有黑名单
	//index:查询起始位置
	public ArrayList<BlackNumberInfo> findPart(int index) {
		//select number,mode from blacknumber limit 0,20; 
		//limit 0,20: 从第0条数据开始,查询20条数据

		SQLiteDatabase database = mHelper.getWritableDatabase();

		//按照_id逆序排列order by _id desc
		//desc:逆序
		//asc:正序
		Cursor cursor = database
				.rawQuery(
						"select number,mode from blacknumber order by _id desc limit ?,20",
						new String[] { index + "" });

		ArrayList<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();

		if (cursor != null) {

			while (cursor.moveToNext()) {
				BlackNumberInfo info = new BlackNumberInfo();

				String number = cursor.getString(0);
				int mode = cursor.getInt(1);

				info.number = number;
				info.mode = mode;

				list.add(info);
			}

			cursor.close();
		}

		database.close();

		return list;
	}

	//获取总数量
	public int getTotalCount() {
		SQLiteDatabase database = mHelper.getWritableDatabase();
		Cursor cursor = database.rawQuery("select count(*) from blacknumber",
				null);

		int totalCount = 0;
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				totalCount = cursor.getInt(0);
			}

			cursor.close();
		}

		database.close();
		return totalCount;
	}

}
