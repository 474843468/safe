package com.itcast.mobilesafe09.test;

import java.util.Random;

import android.test.AndroidTestCase;

import com.itcast.mobilesafe09.db.dao.BlackNumberDao;

/**
 * 黑名单数据库单元测试
 * 
 *   <instrumentation
        android:name="android.test.InstrumentationTestRunner"
        android:targetPackage="com.itcast.mobilesafe09" />
        
     <uses-library android:name="android.test.runner" />
 * 
 */
public class BlackNumberTest extends AndroidTestCase {

	public void testAdd() {
		BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
		//dao.add("110", 1);

		//随机拦截模式
		Random random = new Random();

		//添加100条假数据
		for (int i = 0; i < 100; i++) {
			//随机拦截模式
			int mode = random.nextInt(3) + 1;

			if (i < 10) {
				dao.add("1341234567" + i, mode);
			} else {
				dao.add("135123456" + i, mode);
			}
		}
	}

	public void testDelete() {
		BlackNumberDao.getInstance(getContext()).delete("110");
	}

	public void testUpdate() {
		BlackNumberDao.getInstance(getContext()).update("110", 2);
	}

	public void testFind() {
		boolean exist = BlackNumberDao.getInstance(getContext()).find("110");
		//断言, 参1:期望值;参2:实际值
		assertEquals(true, exist);
	}
}
