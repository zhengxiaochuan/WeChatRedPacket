package com.xiaoyao.redpacket;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;

import org.json.JSONObject;

public class GgActivity extends Activity {
	private SharedPreferences preference;
	private TextView r0, r1;
	private EditText delay, reply;
	private LinearLayout l1, l2, l3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		setContentView(R.layout.activity_gg);
		l1 = (LinearLayout) findViewById(R.id.l1);
		l2 = (LinearLayout) findViewById(R.id.l2);
		l3 = (LinearLayout) findViewById(R.id.l3);

		l1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent t = new Intent(GgActivity.this, ReconfirmActivity.class);
				startActivity(t);
			}
		});
		((TextView) findViewById(R.id.reg_back_bt))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						GgActivity.this.finish();
					}
				});

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		Addg();
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// MainActivity.mSlidingMenu.showMenu(true);
				int returnValue = msg.what;
				closeDialog();
				if (returnValue == 1) {

				} else if (returnValue == 0) {

				} else {
					Toast.makeText(getApplicationContext(), "网络故障，稍后再试", 3)
							.show();
				}
			}
		};
	}

	private ProgressDialog proDialog = null;

	protected void showRequestDialog(String m) {
		if (proDialog != null) {
			proDialog.dismiss();
			proDialog = null;
		}
		proDialog = ProgressDialog.show(this, "提示", "稍等...");

		proDialog.show();

	}

	protected void closeDialog() {
		if (proDialog != null) {
			proDialog.dismiss();
			proDialog = null;
		}
	}

	public void money() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请输入相关信息");
		// 通过LayoutInflater来加载一个xml的布局文件作为一个View对象
		View view = LayoutInflater.from(this).inflate(R.layout.daligo, null);
		// 设置我们自己定义的布局文件作为弹出框的Content
		builder.setView(view);

		final EditText username = (EditText) view.findViewById(R.id.username);
		final EditText password = (EditText) view.findViewById(R.id.password);

		builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String a = username.getText().toString().trim();
				String b = password.getText().toString().trim();
				// 将输入的用户名和密码打印出来
				Toast.makeText(GgActivity.this, "账号: " + a + ", 提示: " + b,
						Toast.LENGTH_SHORT).show();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.show();
	}

	AdView adView;

	public void Addg() {
		String adPlaceId = "2390996"; // 重要：请填上您的广告位ID，代码位错误会导致无法请求到广告
		adView = new AdView(this, adPlaceId);
		// 设置监听器
		adView.setListener(new AdViewListener() {
			public void onAdSwitch() {
				Log.i("gg", "onAdSwitch");
			}

			public void onAdShow(JSONObject info) {
				// 广告已经渲染出来
				Log.i("gg", "onAdShow " + info.toString());
			}

			public void onAdReady(AdView adView) {
				// 资源已经缓存完毕，还没有渲染出来
				Log.i("gg", "onAdReady " + adView);
			}

			public void onAdFailed(String reason) {
				Log.i("gg", "onAdFailed " + reason);
			}

			public void onAdClick(JSONObject info) {
				Log.i("gg", "onAdClick " + info.toString());
				;

			}
		});
		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rllp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		RelativeLayout li = (RelativeLayout) findViewById(R.id.lin);
		li.addView(adView, rllp);
	}

	private Handler mHandler;

}
