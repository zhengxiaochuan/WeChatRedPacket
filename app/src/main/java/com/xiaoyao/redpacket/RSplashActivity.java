package com.xiaoyao.redpacket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.RelativeLayout;

import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashAdListener;

public class RSplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.splash);

		// adUnitContainer
		RelativeLayout adsParent = (RelativeLayout) this.findViewById(R.id.adsRl);

		// the observer of AD
		SplashAdListener listener = new SplashAdListener() {
			@Override
			public void onAdDismissed() {
				Log.i("RSplashActivity", "onAdDismissed");
				jumpWhenCanClick(); // 跳转至您的应用主界面
			}

			@Override
			public void onAdFailed(String arg0) {
				Log.i("RSplashActivity", "onAdFailed " + arg0);
				jump();
			}

			@Override
			public void onAdPresent() {
				Log.i("RSplashActivity", "onAdPresent");
			}

			@Override
			public void onAdClick() {
				Log.i("RSplashActivity", "onAdClick");
				// 设置开屏可接受点击时，该回调可用

			}
		};
		String adPlaceId = "2390996"; // 重要：请填上您的广告位ID，代码位错误会导致无法请求到广告
		new SplashAd(this, adsParent, listener, adPlaceId, true);
	}

	/**
	 * 当设置开屏可点击时，需要等待跳转页面关闭后，再切换至您的主窗口。故此时需要增加canJumpImmediately判断。
	 * 另外，点击开屏还需要在onResume中调用jumpWhenCanClick接口。
	 */
	public boolean canJumpImmediately = false;

	private void jumpWhenCanClick() {
		Log.d("test", "this.hasWindowFocus():" + this.hasWindowFocus());
		if (canJumpImmediately) {
			this.startActivity(new Intent(RSplashActivity.this,
					MainActivity.class));
			this.finish();
		} else {
			canJumpImmediately = true;
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		canJumpImmediately = false;
	}

	/**
	 * 不可点击的开屏，使用该jump方法，而不是用jumpWhenCanClick
	 */
	private void jump() {
		this.startActivity(new Intent(RSplashActivity.this, MainActivity.class));
		this.finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (canJumpImmediately) {
			jumpWhenCanClick();
		}
		canJumpImmediately = true;
	}
}