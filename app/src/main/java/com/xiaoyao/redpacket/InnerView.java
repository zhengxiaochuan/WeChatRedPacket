package com.xiaoyao.redpacket;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.baidu.mobads.InterstitialAd;
import com.baidu.mobads.InterstitialAdListener;

public class InnerView {
	private View view;
	InterstitialAd interAd;

	RelativeLayout rlTempl1;
	private static String adPlaceId = "2390996"; // 双引号中填写自己的广告位ID

	public InnerView(final Context context) {
		view = LayoutInflater.from(context).inflate(R.layout.interstitialad,
				null);
		interAd = new InterstitialAd(context, adPlaceId);
		interAd.setListener(new InterstitialAdListener() {

			@Override
			public void onAdClick(InterstitialAd arg0) {
				Log.i("InterstitialAd", "onAdClick");
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

		Button btn = (Button) view.findViewById(R.id.btn_interstitial);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (interAd.isAdReady()) {
					interAd.showAd((Activity) context);
				} else {
					interAd.loadAd();
				}
			}
		});
	}

	public View getView() {
		return view;
	}
}
