package com.xiaoyao.redpacket;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobads.BaiduManager;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 抢红包主界面
 */

public class MainActivity extends Activity {

	final String service = "com.xiaoyao.redpacket/com.xiaoyao.redpacket.RedService";
	private TextView mTvStart, mTvCount, mTvMoney;
	private Handler mHandler;
	private Timer timer;
	private String mess;
	private int flag;
	private ImageView start;
	/** 福利 */
	private ImageView mIvWelfare;

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
		BaiduManager.init(this);

		setContentView(R.layout.activity_main);
		mTvStart = (TextView) findViewById(R.id.start_button);
        mTvCount = (TextView) findViewById(R.id.tv_count);
        mTvMoney = (TextView) findViewById(R.id.tv_money);

		start = (ImageView) findViewById(R.id.tv);

		((TextView) findViewById(R.id.reg_back_bt))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						exitApp();
					}
				});
		((TextView) findViewById(R.id.tv_setting))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						shake(arg0);
						Intent t = new Intent(getApplicationContext(),
								SettingsActivity.class);
						startActivity(t);
					}
				});
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				mTvStart.setText(mess);
			}
		};
		initListener();
		mIvWelfare = (ImageView) findViewById(R.id.iv_welfare);

		mIvWelfare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent t = new Intent(MainActivity.this, GgActivity.class);
				startActivity(t);
			}
		});
		AnimationDrawable animationDrawable = (AnimationDrawable) mIvWelfare
				.getBackground();
		animationDrawable.start();
	}

	public void shake(View view) {
		Context context = view.getContext();
		Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
		view.startAnimation(shake);
	}

	public void initListener() {
		start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isAccessibilitySettingsOn(getApplicationContext())) {
					writeSharpPreference("status", "1");// 0代表不抢
					open();
				} else if (isAccessibilitySettingsOn(getApplicationContext())) {
					if (readSharpPreference("status").equals("1")) {
						writeSharpPreference("status", "0");// 0代表不抢
						if (timer != null) {
							timer.cancel();
							start.setImageResource(R.drawable.zhi_selector);
							mTvStart.setText(R.string.waiting_for_order_to_rob_red_packet);
						}
					} else {
						start.setImageResource(R.drawable.tt_selector);
						writeSharpPreference("status", "1");// 0代表不抢
						InitTimer();
					}
				}

			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("debug", "onResume");
		if (isAccessibilitySettingsOn(getApplicationContext())
				&& readSharpPreference("status").equals("1")) {
			InitTimer();
			start.setImageResource(R.drawable.tt_selector);

		} else {
			if (timer != null) {
				timer.cancel();
			}
			start.setImageResource(R.drawable.zhi_selector);
			mTvStart.setText(R.string.waiting_for_order_to_rob_red_packet);
		}
        mTvCount.setText("一共抢到红包次数:" + readSharpPreference("count"));
        mTvMoney.setText("一共抢到RMB:" + readSharpPreference("money") + "元");
	}

	public void InitTimer() {
		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (flag == 3) {
					flag = 0;
				}
				mess = getString(R.string.waiting_for_red_packet);
				StringBuffer t = new StringBuffer(mess);
				for (int i = 0; i < flag; i++) {
					t.append("·");
				}
				mess = t.toString();
				flag++;
				mHandler.sendEmptyMessage(0);

			}
		}, new Date(), 1000);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
		}
		com.baidu.mobads.production.BaiduXAdSDKContext.exit();

		Log.i("debug", "退出");
	}

	private void open() {
		try {
			Intent intent = new Intent(
					android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
			startActivity(intent);
			Toast.makeText(this, R.string.open_setting_tips, Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isAccessibilitySettingsOn(Context mContext) {
		int accessibilityEnabled = 0;
		boolean accessibilityFound = false;
		try {
			accessibilityEnabled = Settings.Secure.getInt(mContext
							.getApplicationContext().getContentResolver(),
					android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
			// Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
		} catch (Exception e) {
			// Log.e(TAG,
			// "Error finding setting, default accessibility to not found: "
			// + e.getMessage());
		}
		TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(
				':');

		if (accessibilityEnabled == 1) {
			// Log.v(TAG, "***ACCESSIBILIY IS ENABLED*** -----------------");
			String settingValue = Settings.Secure.getString(mContext
							.getApplicationContext().getContentResolver(),
					Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
			if (settingValue != null) {
				TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
				splitter.setString(settingValue);
				while (splitter.hasNext()) {
					String accessabilityService = splitter.next();

					// Log.v(TAG, "-------------- > accessabilityService :: " +
					// accessabilityService);
					if (accessabilityService.equalsIgnoreCase(service)) {
						// Log.v(TAG,
						// "We've found the correct setting - accessibility is switched on!");
						return true;
					}
				}
			}
		} else {
			// Log.v(TAG, "***ACCESSIBILIY IS DISABLED***");
		}

		return accessibilityFound;
	}

	public void writeSharpPreference(String key, String value) {
		SharedPreferences preference = getSharedPreferences("wechatred",
				MODE_PRIVATE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(key, value);
		editor.commit();

	}

	public String readSharpPreference(String key) {
		SharedPreferences preference = getSharedPreferences("wechatred",
				MODE_PRIVATE);

		String value = preference.getString(key, "");
		if (key.equals("delay")) {
			value = preference.getString(key, "0.1");
		}
		if (key.equals("money")) {
			value = preference.getString(key, "0.0");
		}
		if (key.equals("count")) {
			value = preference.getString(key, "0");
		}
		if (key.equals("status")) {
			value = preference.getString(key, "1");
		}
		return value;

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			exitApp();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

    /**
     * 退出应用
     */
	public void exitApp() {
		AlertDialog.Builder builder = new Builder(this);
		AlertDialog dialog;
		// 设置对话框图标，可以使用自己的图片，Android本身也提供了一些图标供我们使用

		// 设置对话框标题
		builder.setTitle("提示");
		// 设置对话框内的文本
		builder.setMessage("您不下达停止的指令，退出后依旧可以后台抢红包哟！");
		// 设置确定按钮，并给按钮设置一个点击侦听，注意这个OnClickListener使用的是DialogInterface类里的一个内部接口
		builder.setPositiveButton("好滴", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				// 执行点击确定按钮的业务逻辑
				MainActivity.this.finish();
			}

		});
		// 设置取消按钮
		builder.setNeutralButton("积累红包再走",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent t = new Intent(MainActivity.this,
								ReconfirmActivity.class);
						startActivity(t);
					}

				});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				// 执行点击取消按钮的业务逻辑
				if (dialog != null) {
					dialog.dismiss();
				}
			}

		});
		// 使用builder创建出对话框对象
		dialog = builder.create();
		// 显示对话框
		dialog.show();
	}
}
