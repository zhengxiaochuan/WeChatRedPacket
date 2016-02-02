package com.xiaoyao.redpacket;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.androidquery.AQuery;
import com.baidu.mobad.feeds.BaiduNative;
import com.baidu.mobad.feeds.BaiduNative.BaiduNativeNetworkListener;
import com.baidu.mobad.feeds.NativeErrorCode;
import com.baidu.mobad.feeds.NativeResponse;
import com.baidu.mobad.feeds.RequestParameters;

import java.util.List;

public class NativeView {
	private View view;
	RelativeLayout rlTempl1;
	private static String YOUR_AD_PLACE_ID = "2390997"; // 双引号中填写自己的广告位ID

	public NativeView(final Context context) {
		view = LayoutInflater.from(context).inflate(
				R.layout.nativeorigin_activity, null);
		rlTempl1 = (RelativeLayout) view.findViewById(R.id.rlTemplate1);

		Button btnFetch = (Button) view.findViewById(R.id.btn_fetch);
		btnFetch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				fetchAd(context);
			}

		});
		fetchAd(context);
	}

	public View getView() {
		return view;
	}

	public void fetchAd(final Context activity) {
		// Log.i("demo", "NativeOriginActivity.fetchAd");
		/**
		 * Step 1. 创建BaiduNative对象，参数分别为： 上下文context，广告位ID,
		 * BaiduNativeNetworkListener监听（监听广告请求的成功与失败）
		 * 注意：请将YOUR_AD_PALCE_ID替换为自己的广告位ID
		 */
		BaiduNative baidu = new BaiduNative(activity, YOUR_AD_PLACE_ID,
				new BaiduNativeNetworkListener() {

					@Override
					public void onNativeFail(NativeErrorCode arg0) {
						Log.i("NativeOriginActivity", "onNativeFail reason:"
								+ arg0.name());
					}

					@Override
					public void onNativeLoad(List<NativeResponse> arg0) {
						// 一个广告只允许展现一次，多次展现、点击只会计入一次
						if (arg0.size() > 0) {
							// demo仅简单地显示一条。可将返回的多条广告保存起来备用。
							updateView(arg0.get(0), activity);
						}

					}

				});

		/**
		 * Step 2. 创建requestParameters对象，并将其传给baidu.makeRequest来请求广告
		 */
		// 用户点击下载类广告时，是否弹出提示框让用户选择下载与否
		RequestParameters requestParameters = new RequestParameters.Builder()
				.keywords("游戏,职场").confirmDownloading(true).build();

		baidu.makeRequest(requestParameters);
	}

	public void updateView(final NativeResponse nativeResponse, Context con) {
		// Log.i("demo", "NativeOriginActivity.updateView");

		// use template1
		AQuery aq = new AQuery(con);
		aq.id(R.id.iv_title).text(nativeResponse.getTitle());
		aq.id(R.id.iv_icon).image(nativeResponse.getIconUrl());
		aq.id(R.id.iv_main).image(nativeResponse.getImageUrl());
		// 警告：调用该函数来发送展现，勿漏！
		nativeResponse.recordImpression(rlTempl1);
		rlTempl1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// 点击响应
				nativeResponse.handleClick(view);
			}
		});

		/*
		 * //use
		 * template2。特别提醒：当您选择该模板时，desiredAssets需设置为不需要NativeAdAsset.MAIN_IMAGE
		 * ，填充会更加充足。 aq.id(R.id.iv_title2).text(nativeResponse.getTitle());
		 * aq.id(R.id.iv_desc2).text(nativeResponse.getDesc());
		 * aq.id(R.id.iv_icon2).image(nativeResponse.getIconUrl());
		 * aq.id(R.id.iv_cta).text(nativeResponse.isDownloadApp()? "免费下载" :
		 * "查看详情");
		 * 
		 * nativeResponse.recordImpression(rlTempl2);// 发送展示日志
		 * 
		 * rlTempl2.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View view) {
		 * nativeResponse.handleClick(view);// 点击响应 } });
		 */
	}
}
