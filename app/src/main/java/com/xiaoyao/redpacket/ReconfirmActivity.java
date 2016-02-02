package com.xiaoyao.redpacket;

import android.app.Activity;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.baidu.mobads.InterstitialAd;
import com.baidu.mobads.InterstitialAdListener;

import org.json.JSONObject;

public class ReconfirmActivity extends Activity {
	private Handler mHandler;

	/**
	 */
	private static String YOUR_AD_PLACE_ID = "2390996"; // 双引号中填写自己的广告位ID
	private boolean a[] = new boolean[4];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("demo", "ListViewActivity.onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		setContentView(R.layout.listview_activity);
		Addg("2390995", 0);
		Addg("2390996", 1);
		Addg("2390997", 2);
		click(3);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// MainActivity.mSlidingMenu.showMenu(true);
				int returnValue = msg.what;
				if (interAd.isAdReady()) {
					interAd.showAd(ReconfirmActivity.this);
				} else {
					interAd.loadAd();
				}
			}
		};
		((TextView) findViewById(R.id.reg_back_bt))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						ReconfirmActivity.this.finish();
					}
				});
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				click(3);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mHandler.sendEmptyMessage(0);
			}
		}).start();
	}

	public void Addg(String id, final int i) {
		String adPlaceId = id; // 重要：请填上您的广告位ID，代码位错误会导致无法请求到广告
		AdView adView = new AdView(this, adPlaceId);
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
				if (!a[i]) {
					a[i] = true;
				}

			}
		});
		// 将adView添加到父控件中(注：该父控件不一定为您的根控件，只要该控件能通过addView能添加广告视图即可)
		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		// rllp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		LinearLayout li = (LinearLayout) findViewById(R.id.lin);
		li.addView(adView, rllp);
	}

	public void click(final int i) {
		String adPlaceId = "2390997"; // 重要：请填上您的广告位ID，代码位错误会导致无法请求到广告
		interAd = new InterstitialAd(this, adPlaceId);
		interAd.setListener(new InterstitialAdListener() {

			@Override
			public void onAdClick(InterstitialAd arg0) {
				Log.i("InterstitialAd", "onAdClick");
				if (!a[i]) {

					a[i] = true;
				}

			}

			@Override
			public void onAdDismissed() {
				Log.i("InterstitialAd", "onAdDismissed");
				interAd.loadAd();
			}

			@Override
			public void onAdFailed(String arg0) {
				Log.i("InterstitialAd", "onAdFailed");
			}

			@Override
			public void onAdPresent() {
				Log.i("InterstitialAd", "onAdPresent");

			}

			@Override
			public void onAdReady() {
				Log.i("InterstitialAd", "onAdReady");
			}

		});
		interAd.loadAd();

	}

	InterstitialAd interAd;

}
