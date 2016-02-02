package com.shangx.hb_money_app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobads.AdSettings;
import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.baidu.mobads.InterstitialAd;
import com.baidu.mobads.InterstitialAdListener;

import org.json.JSONObject;

/**
 * 使用教程页面
 */
public class UsageTutorialActivity extends Activity {

    /** 广告控件 */
    private AdView adView;

    private InterstitialAd interAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.usage_tutorial);

        initBanner();

        ad();

        // 返回按钮
        TextView tvBack = (TextView) this.findViewById(R.id.tv_back);
        tvBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        chabo();
    }

    public void chabo() {

        if (interAd.isAdReady()) {
            interAd.showAd(UsageTutorialActivity.this);
        } else {
            interAd.loadAd();
        }
    }

    private void ad() {
        String adPlaceId = "2373336"; // 重要：不填写代码位id不能出广告
        interAd = new InterstitialAd(this, adPlaceId);
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
    }

    private void initBanner() {
        LinearLayout adLayout = (LinearLayout) findViewById(R.id.ll_ad);
//       AdView adView = new AdView(this, AdSize.FIT_SCREEN);
//       adLayout.addView(adView);
//       EcManager.startFloatWindowService(this);

        // 代码设置AppSid和Appsec，此函数必须在AdView实例化前调用
        // AdView.setAppSid("debug");
        // AdView.setAppSec("debug");

        // 人群属性
        AdSettings.setKey(new String[]{"baidu", "中 国 "});
        // AdSettings.setSex(AdSettings.Sex.FEMALE);
        // AdSettings.setBirthday(Calendar.getInstance());
        // AdSettings.setCity("上海");
        // AdSettings.setZip("123456");
        // AdSettings.setJob("工程师");
        // AdSettings.setEducation(AdSettings.Education.BACHELOR);
        // AdSettings.setSalary(AdSettings.Salary.F10kT15k);
        // AdSettings.setHob(new String[]{"羽毛球", "足球", "baseball"});
        // AdSettings.setUserAttr("k1","v1");
        // AdSettings.setUserAttr("k2","v2");

        // 创建广告View
        String adPlaceId = "2373334"; // 重要：不填写代码位id不能出广告
        adView = new AdView(this, adPlaceId);
        // 设置监听器
        adView.setListener(new AdViewListener() {
            public void onAdSwitch() {
                Log.w("", "onAdSwitch");
            }

            public void onAdShow(JSONObject info) {
                // 广告已经渲染出来
                Log.w("", "onAdShow " + info.toString());
            }

            public void onAdReady(AdView adView) {
                // 资源已经缓存完毕，还没有渲染出来
                Log.w("", "onAdReady " + adView);
            }

            public void onAdFailed(String reason) {
                Log.w("", "onAdFailed " + reason);
            }

            public void onAdClick(JSONObject info) {
                Log.w("", "onAdClick " + info.toString());
            }

            public void onVideoStart() {
                Log.w("", "onVideoStart");
            }

            public void onVideoFinish() {
                Log.w("", "onVideoFinish");
            }
        });

        //将adView添加到父控件中(注：该父控件不一定为您的根控件，只要该控件能通过addView能添加广告视图即可)
//		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//		rllp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        adLayout.addView(adView);
    }
}
