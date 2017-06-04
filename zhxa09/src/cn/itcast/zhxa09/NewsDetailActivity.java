package cn.itcast.zhxa09;

import java.io.File;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.itcast.zhxa09.global.GlobalConstants;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;

public class NewsDetailActivity extends Activity {

	private ImageView ivMenu;
	private TextView tvTitle;
	private ImageView ivTextSize;
	private ImageView ivShare;
	private WebView webView;
	private ProgressBar pb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_news_detail);

		ivMenu = (ImageView) findViewById(R.id.ivMenu);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		ivTextSize = (ImageView) findViewById(R.id.ivTextSize);
		ivShare = (ImageView) findViewById(R.id.ivShare);

		pb = (ProgressBar) findViewById(R.id.pb);

		ivMenu.setImageResource(R.drawable.back);
		ivMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ivTextSize.setVisibility(View.VISIBLE);
		ivShare.setVisibility(View.VISIBLE);

		ivTextSize.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 弹出选择框
				showChooseDialog();
			}
		});

		ivShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				share();

			}
		});

		webView = (WebView) findViewById(R.id.webView);
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		String url = getIntent().getStringExtra("url");

		webView.loadUrl(GlobalConstants.URL_PREFIX + url);

		webView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onReceivedTitle(WebView view, String title) {
				tvTitle.setText(title);
				super.onReceivedTitle(view, title);
			}

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
			}
		});

		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				pb.setVisibility(View.VISIBLE);
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				pb.setVisibility(View.GONE);
				super.onPageFinished(view, url);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

		});

	}

	protected void share() {
		// 1、利用系统分享的功能
		/*
		 * Intent sendIntent = new Intent();
		 * sendIntent.setAction(Intent.ACTION_SEND);
		 * sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
		 * sendIntent.setType("text/plain");
		 * startActivity(Intent.createChooser(sendIntent, "请选择你所需要的应用程序"));
		 */

		/*
		 * Intent sendIntent = new Intent();
		 * sendIntent.setAction(Intent.ACTION_SEND);
		 * sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new
		 * File("/sdcard/beauty1.jpg"))); sendIntent.setType("image/jpeg");
		 * startActivity(Intent.createChooser(sendIntent, "请选择你所需要的应用程序"));
		 */
		// 系统分享的弊端
		// 1、布局修改不了 2、没有安装的程序无法显示

		// 2、第三方分享 分享到第三方社交平台 ShareSDK-->是将第三方平台整合

		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		oks.setTheme(OnekeyShareTheme.CLASSIC);//设置主题
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("第三方分享");
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("我是分享文本");
		// 分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
		oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImagePath("/sdcard/beauty1.jpg");//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://sharesdk.cn");

		// 启动分享GUI
		oks.show(this);

	}

	private int tempWhitch = 2;
	private WebSettings settings;

	protected void showChooseDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("字体设置");
		settings = webView.getSettings();
		String[] items = new String[] { "超大号字体", "大号字体", "正常字体", "小号字体",
				"超小号字体" };
		builder.setSingleChoiceItems(items, tempWhitch,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						tempWhitch = which;
					}
				});

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (tempWhitch) {
				case 0:
					settings.setTextSize(WebSettings.TextSize.LARGEST);
					break;
				case 1:
					settings.setTextSize(WebSettings.TextSize.LARGER);
					break;
				case 2:
					settings.setTextSize(WebSettings.TextSize.NORMAL);
					break;
				case 3:
					settings.setTextSize(WebSettings.TextSize.SMALLER);
					break;
				case 4:
					settings.setTextSize(WebSettings.TextSize.SMALLEST);
					break;

				}
			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});

		builder.create().show();

	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}


}
