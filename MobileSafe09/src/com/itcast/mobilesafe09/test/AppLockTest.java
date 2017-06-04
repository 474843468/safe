package com.itcast.mobilesafe09.test;

import android.test.AndroidTestCase;

import com.itcast.mobilesafe09.db.dao.AppLockDao;

/**
 * 程序锁数据库单元测试
 * 
 *   <instrumentation
        android:name="android.test.InstrumentationTestRunner"
        android:targetPackage="com.itcast.mobilesafe09" />
        
     <uses-library android:name="android.test.runner" />
 * 
 */
public class AppLockTest extends AndroidTestCase {

	public void testAdd() {
		AppLockDao.getInstance(getContext()).add("com.android.mms");
		AppLockDao.getInstance(getContext()).add("com.android.calculator2");
	}

}
