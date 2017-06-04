package com.itcast.mobilesafe09.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 归属地数据库查询
 */
public class AddressDao {

	public static String getAddress(Context ctx, String number) {
		String address = "未知号码";

		//查询数据库
		//参1:数据库文件的本地路径; 参2:游标工厂, 参3:访问模式:只读
		SQLiteDatabase database = SQLiteDatabase.openDatabase(ctx.getFilesDir()
				+ "/address.db", null, SQLiteDatabase.OPEN_READONLY);

		//database.query(table, columns, selection, selectionArgs, groupBy, having, orderBy)

		//判断当前号码是否是手机号码
		//1[3-8]+ 9位数字: 正则表达式
		//^1[3-8]\d{9}$
		if (number.matches("^1[3-8]\\d{9}$")) {//是否是手机号码
			//直接执行一条sql语句
			Cursor cursor = database
					.rawQuery(
							"select location from data2 where id=(select outkey from data1 where id=?)",
							new String[] { number.substring(0, 7) });

			if (cursor != null) {
				//只有一条记录, 无需while循环
				if (cursor.moveToFirst()) {//指向第一条记录
					address = cursor.getString(0);
				}

				cursor.close();
			}
		} else {
			System.out.println("不是手机号码");

			switch (number.length()) {
			case 3:
				//报警电话
				address = "报警电话";
				break;
			case 4:
				//模拟器
				address = "模拟器";
				break;
			case 5:
				//客服电话
				address = "客服电话";
				break;

			case 7:
			case 8:
				//本地电话
				address = "本地电话";
				break;
			default:
				//长途电话
				// 0910 8888 8888 10-12
				// 010 8888888
				if (number.startsWith("0") && number.length() >= 10
						&& number.length() <= 12) {//有可能是长途电话
					//select location from data2 where area = 10
					//先查询4位区号:
					//如果没有查询出4位区号, 再查询3位区号
					Cursor cursor = database.rawQuery(
							"select location from data2 where area = ?",
							new String[] { number.substring(1, 4) });

					if (cursor != null) {
						if (cursor.moveToFirst()) {
							//查询出了4位区号
							address = cursor.getString(0);
						}

						cursor.close();
					}

					//如果没有查询出4位区号, 再查询3位区号
					if ("未知号码".equals(address)) {
						cursor = database.rawQuery(
								"select location from data2 where area = ?",
								new String[] { number.substring(1, 3) });

						if (cursor != null) {
							if (cursor.moveToFirst()) {
								//查询到了3位区号
								address = cursor.getString(0);
							}

							cursor.close();
						}
					}
				}

				break;
			}

		}

		database.close();

		return address;
	}
}
