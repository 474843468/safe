package cn.itcast.zhxa09.utils;

import android.content.Context;

/**
 * 缓存的工具类
 * @author zhengping
 *
 */
public class CacheUtils {
	
	//存缓存  以url为key 将json数据存储起来
	public static void setCache(Context ctx,String url,String json) {
		//1、使用什么方式   a、sp  2、sqlite 3、文件
		//sp使用场景：配置信息
		//sqlite使用：存储有关系的一些数据
		//文件：存储json数据      sdcard    /data/data/包名/ 
		
		//2、存在哪里  /data/data/包名/
		
		PrefUtils.setString(ctx, url, json);
	}
	
	//取缓存
	public static String getCache(Context ctx,String url) {
		return PrefUtils.getString(ctx, url, null);
	}

}
