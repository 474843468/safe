package cn.itcast.zhxa09.global;

/**
 * 维护全局变量
 * 
 * @author zhengping
 * 
 */
public class GlobalConstants {

	//10.0.2.2-->预留给原生的模拟器使用的访问电脑的ip地址
	//10.0.3.2-->GenyMotion
	//
	
	public static final String URL_PREFIX = "http://192.168.13.100:8080/zhbj";
	public static final String URL_CATEGORY = URL_PREFIX + "/categories.json";
	public static final String URL_PHOTOS = URL_PREFIX + "/photos/photos_1.json";

}
