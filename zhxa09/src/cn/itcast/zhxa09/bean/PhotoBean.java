package cn.itcast.zhxa09.bean;

import java.util.ArrayList;

public class PhotoBean {

	public PhotoData data;
	public String retcode;

	public class PhotoData {
		public String countcommenturl;
		public String more;
		public ArrayList<PhotoItemData> news;
		public String title;
	}

	public class PhotoItemData {
		public String comment;// true,
		public String commentlist;// /static/api/news/10003/72/82772/comment_1.json,
		public String commenturl;// /client/user/newComment/82772,
		public String id;// 82772,
		public String largeimage;// /static/images/2014/11/07/70/476518773M7R.jpg,
		public String listimage;// /photos/images/46728356JDGO.jpg,
		public String pubdate;// 2014-11-07 11;//40,
		public String smallimage;// /static/images/2014/11/07/79/485753989TVL.jpg,
		public String title;// 北京·APEC绚丽之夜,
		public String type;// news,
		public String url;// /static/html/2014/11/07/7743665E4E6B10766F26.html

	}

}
