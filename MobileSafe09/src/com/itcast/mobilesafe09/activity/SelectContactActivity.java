package com.itcast.mobilesafe09.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.itcast.mobilesafe09.R;

/**
 * 选择联系人
 */
public class SelectContactActivity extends Activity {

	private ListView lvList;
	private ArrayList<HashMap<String, String>> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contact);
		lvList = (ListView) findViewById(R.id.lv_list);

		//读取数据库操作,建议放在子线程处理
		new Thread() {

			public void run() {
				list = readContact();

				//不能在子线程直接更新主界面ui
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						//(了解)参2:数据;参3:条目布局文件id;参4:数据在map集合中字段的名称;参5:对应map字段的控件的id
						lvList.setAdapter(new SimpleAdapter(
								SelectContactActivity.this, list,
								R.layout.item_contact, new String[] { "name",
										"phone" }, new int[] { R.id.tv_name,
										R.id.tv_phone }));
					}
				});
			};
		}.start();

		lvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HashMap<String, String> map = list.get(position);
				String phone = map.get("phone");//取出电话号码

				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				setResult(RESULT_OK, intent);//将结果回传给上个页面

				finish();
			}
		});
	}

	//读取联系人数据
	//  <uses-permission android:name="android.permission.READ_CONTACTS" />
	// <uses-permission android:name="android.permission.WRITE_CONTACTS" />
	private ArrayList<HashMap<String, String>> readContact() {
		//系统联系人数据库
		//raw_contacts, data, mimetypes
		//1. raw_contacts: 保存联系人id
		//2. data: 保存联系人数据
		//3. mimetypes: 保存数据类型
		Uri rawUri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri dataUri = Uri.parse("content://com.android.contacts/data");

		//查询raw_contacts, 获取联系人id
		//参1:uri; 参2:要查询哪些字段;参3:过滤条件;参4:过滤条件的参数值; 参5:排序
		Cursor cursor = getContentResolver().query(rawUri,
				new String[] { "contact_id" }, null, null, null);

		//保存所有联系人数据
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		if (cursor != null) {

			while (cursor.moveToNext()) {
				String rawContactId = cursor.getString(0);//取出联系人id

				//根据id查询联系人数据, data表
				Cursor dataCursor = getContentResolver()
						.query(dataUri, new String[] { "data1", "mimetype" },
								"raw_contact_id=?",
								new String[] { rawContactId }, null);

				if (dataCursor != null) {
					//保存单个联系人的所有类型的数据
					HashMap<String, String> map = new HashMap<String, String>();
					while (dataCursor.moveToNext()) {
						String data = dataCursor.getString(0);//数据
						String type = dataCursor.getString(1);//类型

						if ("vnd.android.cursor.item/phone_v2".equals(type)) {
							//电话号码
							map.put("phone", data);
						} else if ("vnd.android.cursor.item/name".equals(type)) {
							//姓名
							map.put("name", data);
						}
					}

					//添加一个联系人数据
					//过滤掉电话号码或联系人为空的数据
					if (map.get("phone") != null && map.get("name") != null) {
						list.add(map);
					}

					dataCursor.close();
				}
			}

			cursor.close();
		}

		return list;
	}

}
