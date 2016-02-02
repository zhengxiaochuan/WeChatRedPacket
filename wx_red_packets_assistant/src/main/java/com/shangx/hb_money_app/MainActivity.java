package com.shangx.hb_money_app;

import java.util.List;

import org.json.JSONObject;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobads.AdSettings;
import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.baidu.mobads.InterstitialAd;
import com.baidu.mobads.InterstitialAdListener;

/**
 * 主页面
 */
public class MainActivity extends Activity {

    private TextView mAccessibleLabel;
    private TextView mNotificationLabel;
    private TextView mLabelText;

    private static final Intent sSettingsIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final float density = metrics.density;
        final int screenWidth = metrics.widthPixels;


        int width = (int) (screenWidth - (density * 12 + .5f) * 2);
        int height = (int) (366.f * width / 1080);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
//        ImageView imageView1 = (ImageView) findViewById(R.id.image_accessibility);
//        ImageView imageView2 = (ImageView) findViewById(R.id.image_notification);

        mAccessibleLabel = (TextView) findViewById(R.id.label_accessible);
        mNotificationLabel = (TextView) findViewById(R.id.label_notification);
        mLabelText = (TextView) findViewById(R.id.label_text);

//        imageView1.setLayoutParams(lp);
//        imageView2.setLayoutParams(lp);

        if (Build.VERSION.SDK_INT >= 18) {
//            imageView2.setVisibility(View.VISIBLE);
            mNotificationLabel.setVisibility(View.VISIBLE);
            findViewById(R.id.button_notification).setVisibility(View.VISIBLE);
        } else {
//            imageView2.setVisibility(View.GONE);
            mNotificationLabel.setVisibility(View.GONE);
            findViewById(R.id.button_notification).setVisibility(View.GONE);
        }

//        imageView1.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.i("test", "# fired");
//                unlockScreen();
//
//            }
//        }, 5000L);
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeLabelStatus();
    }

    private void changeLabelStatus() {
        boolean isAccessibilityEnabled = isAccessibleEnabled();
        mAccessibleLabel.setTextColor(isAccessibilityEnabled ? 0xFF009588 : Color.RED);
        mAccessibleLabel.setText(isAccessibleEnabled() ? "辅助功能已打开" : "辅助功能未打开");
        mLabelText.setText(isAccessibilityEnabled ? "好了~你可以去做其他事情了，我会自动给你抢红包的" : "请打开开关开始抢红包");

        if (Build.VERSION.SDK_INT >= 18) {
            boolean isNotificationEnabled = isNotificationEnabled();
            mNotificationLabel.setTextColor(isNotificationEnabled ? 0xFF009588 : Color.RED);
            mNotificationLabel.setText(isNotificationEnabled ? "接收通知已打开" : "接收通知未打开");

            if (isAccessibilityEnabled && isNotificationEnabled) {
                mLabelText.setText("好了~你可以去做其他事情了，我会自动给你抢红包的");
            } else {
                mLabelText.setText("请把两个开关都打开开始抢红包");
            }
        }
    }

    public void onStartDetail(View view) {
        startActivity(new Intent(MainActivity.this, UsageTutorialActivity.class));
    }

    public void onNotificationEnableButtonClicked(View view) {
        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
    }

    public void onSettingsClicked(View view) {
        startActivity(sSettingsIntent);
    }

    private boolean isAccessibleEnabled() {
        AccessibilityManager manager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = manager.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo info : runningServices) {
            if (info.getId().equals(getPackageName() + "/.MonitorService")) {
                return true;
            }
        }
        return false;
    }

    private boolean isNotificationEnabled() {
        ContentResolver contentResolver = getContentResolver();
        String enabledListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");

        if (!TextUtils.isEmpty(enabledListeners)) {
            return enabledListeners.contains(getPackageName() + "/" + getPackageName() + ".NotificationService");
        } else {
            return false;
        }
    }

    private void showEnableAccessibilityDialog() {
        final ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setTitle("重要!").setMessage("您需要打开\"有红包\"的辅助功能选项才能抢微信红包")
                .setPositiveButton("打开", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(sSettingsIntent);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", null);
        dialog.show();
    }

    private long mExitTime;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();

            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
