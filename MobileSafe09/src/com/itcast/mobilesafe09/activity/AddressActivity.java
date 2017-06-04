package com.itcast.mobilesafe09.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.itcast.mobilesafe09.R;
import com.itcast.mobilesafe09.db.dao.AddressDao;
import com.itcast.mobilesafe09.utils.ToastUtils;

/**
 * 归属地查询页面
 */
public class AddressActivity extends Activity {

	private EditText etInput;
	private Button btnQuery;
	private TextView tvAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);

		etInput = (EditText) findViewById(R.id.et_number);
		btnQuery = (Button) findViewById(R.id.btn_query);
		tvAddress = (TextView) findViewById(R.id.tv_result);

		btnQuery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String number = etInput.getText().toString().trim();

				if (!TextUtils.isEmpty(number)) {
					//查询归属地
					String address = AddressDao.getAddress(
							getApplicationContext(), number);
					tvAddress.setText(address);
				} else {
					ToastUtils.showToast(getApplicationContext(), "输入内容不能为空!");

					//抖动动画
					//将xml文件生成动画对象
					Animation shake = AnimationUtils.loadAnimation(
							getApplicationContext(), R.anim.shake);

					//自定义插补器
					//					shake.setInterpolator(new Interpolator() {
					//
					//						//x表示输入, y表示输出
					//						@Override
					//						public float getInterpolation(float x) {
					//
					//							//此处可以定义y和x的关系, 用方程式表示
					//							//y = ax + b;
					//							float y = x;
					//
					//							return y;
					//						}
					//					});

					//启动动画
					etInput.startAnimation(shake);

					vibrate();
				}

			}
		});

		//监听文本框内容变化
		etInput.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				System.out.println("文本发生变化了");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				System.out.println("文本发生变化之前");
			}

			@Override
			public void afterTextChanged(Editable s) {
				System.out.println("文本发生变化之后");
				//自动查询归属地
				String number = s.toString();
				tvAddress.setText(AddressDao.getAddress(
						getApplicationContext(), number));
			}
		});
	}

	//让手机震动
	//android.permission.VIBRATE
	private void vibrate() {
		//振动器
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(2000);//震动2秒
		//vibrator.vibrate(new long[] { 2000, 3000, 1000, 2000 }, 0);//从1开始数的话, 奇数位表示停留时间, 偶数位表示震动时间
		//参2:表示重复方式, -1表示不重复; 参2表示重复开始的位置, 如果传0,表示从头开始重复;如果传1表示从第二个参数位置开始重复
		//vibrator.cancel();//停止震动
	}

}
