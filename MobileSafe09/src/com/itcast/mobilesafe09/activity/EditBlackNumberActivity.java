package com.itcast.mobilesafe09.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.itcast.mobilesafe09.R;
import com.itcast.mobilesafe09.db.dao.BlackNumberDao;
import com.itcast.mobilesafe09.utils.ToastUtils;

/**
 * 黑名单编辑页面, 添加/更新
 * 
 * 开发流程:
 * 1. 布局开发, RadioGroup + RadioButton, 单选效果
 * 2. 添加黑名单, 数据库保存
 * 3. 数据库逆序排列, order by _id desc
 * 4. 数据回显, startActivityForResult, 更新集合, 刷新ListView
 * 5. 区分添加还是更新, 通过intent传参数
 * 6. 更新黑名单页面数据显示及界面更新
 * 7. 更新黑名单逻辑, 数据库
 * 8. 更新黑名单数据回显
 */
public class EditBlackNumberActivity extends Activity implements
		OnClickListener {

	private EditText etNumber;
	private RadioGroup rgGroup;
	private BlackNumberDao mDao;
	private TextView tvTitle;
	private Button btnOk;

	private boolean isUpdate;//标记当前是否是更新

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_black_number);

		mDao = BlackNumberDao.getInstance(this);

		btnOk = (Button) findViewById(R.id.btn_ok);
		btnOk.setOnClickListener(this);
		findViewById(R.id.btn_cancel).setOnClickListener(this);

		etNumber = (EditText) findViewById(R.id.et_number);
		rgGroup = (RadioGroup) findViewById(R.id.rg_group);
		tvTitle = (TextView) findViewById(R.id.tv_title);

		//获取上一个页面传过来的参数
		isUpdate = getIntent().getBooleanExtra("isUpdate", false);

		if (isUpdate) {
			System.out.println("更新黑名单了....");
			tvTitle.setText("更新黑名单");
			etNumber.setEnabled(false);//文本框不可用

			//回显待更新的数据
			String number = getIntent().getStringExtra("number");
			int mode = getIntent().getIntExtra("mode", 1);//1,2,3

			etNumber.setText(number);

			switch (mode) {
			case 1:
				rgGroup.check(R.id.rb_phone);
				break;
			case 2:
				rgGroup.check(R.id.rb_sms);
				break;
			case 3:
				rgGroup.check(R.id.rb_all);
				break;
			}

			//修改按钮名称
			btnOk.setText("更新");
		} else {
			System.out.println("添加黑名单了....");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok:

			String number = etNumber.getText().toString().trim();

			if (TextUtils.isEmpty(number)) {
				ToastUtils.showToast(this, "输入内容不能为空!");
				return;
			}

			//获取被选中的RadioButton的id
			int checkedRadioButtonId = rgGroup.getCheckedRadioButtonId();
			int mode = 1;//拦截模式,默认拦截电话
			switch (checkedRadioButtonId) {
			case R.id.rb_phone:
				mode = 1;
				break;
			case R.id.rb_sms:
				mode = 2;
				break;
			case R.id.rb_all:
				mode = 3;
				break;
			}

			if (!isUpdate) {
				//保存黑名单
				boolean success = mDao.add(number, mode);

				if (success) {
					ToastUtils.showToast(this, "保存成功!");

					Intent intent = new Intent();
					intent.putExtra("number", number);
					intent.putExtra("mode", mode);

					//将结果回传给上一个页面
					setResult(RESULT_OK, intent);

					finish();
				} else {
					ToastUtils.showToast(this, "保存失败!");
				}

			} else {
				//更新黑名单
				boolean update = mDao.update(number, mode);

				if (update) {
					ToastUtils.showToast(this, "更新成功!");

					Intent intent = new Intent();
					intent.putExtra("number", number);
					intent.putExtra("mode", mode);

					//将结果回传给上一个页面
					setResult(RESULT_OK, intent);

					finish();
				} else {
					ToastUtils.showToast(this, "更新失败!");
				}

			}

			break;
		case R.id.btn_cancel:
			finish();
			break;

		default:
			break;
		}
	}

}
