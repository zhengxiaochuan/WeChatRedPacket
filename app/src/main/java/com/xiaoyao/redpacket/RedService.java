package com.xiaoyao.redpacket;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

/**
 * <p>
 * Created by 李文龙(LeonLee) on 15/2/17 下午10:25.
 * </p>
 * <p>
 * <a href="mailto:codeboy2013@163.com">Email:codeboy2013@163.com</a>
 * </p>
 *
 * 抢红包外挂服务
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class RedService extends AccessibilityService {
	// 声明键盘管理器
	KeyguardManager mKeyguardManager = null;
	// 声明键盘锁
	private KeyguardLock mKeyguardLock = null;
	// 声明电源管理器
	private PowerManager pm;
	private PowerManager.WakeLock wakeLock;
	static final String TAG = "QiangHongBao";

	/** 微信的包名 */
	static final String WECHAT_PACKAGENAME = "com.tencent.mm";
	/** 红包消息的关键字 */
	static final String HONGBAO_TEXT_KEY = "[微信红包]";
	private boolean isClicked;
	private boolean isFirstChecked;// 表示是红包软件去抢的 如果在聊天页面自己抢的 不做任何操作 退出那些都不管
	Handler handler = new Handler();

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		final int eventType = event.getEventType();

		// Log.d("debug", "事件---->" + event);
		String status = readSharpPreference("status");
		// 通知栏事件
		if (status.equals("1")) {
			if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
				// setRedRing();

				setDelay();
				Lock();
				List<CharSequence> texts = event.getText();
				if (!texts.isEmpty()) {
					for (CharSequence t : texts) {
						String text = String.valueOf(t);
						if (text.contains(HONGBAO_TEXT_KEY)) {
							play();
							openNotify(event);
							break;
						}
					}
				}
			} else if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

				openHongBao(event);
			}
		} else {
			Log.i("debug", "来了 不抢");
		}
		// if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
		// Log.i("debug", isFirstChecked
		// + "AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED");
		//
		// // openHongBao(event);
		// }
		// if (eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
		// Log.i("debug", isFirstChecked
		// + "AccessibilityEvent.TYPE_VIEW_SCROLLED");
		//
		// // openHongBao(event);
		// }
		// if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
		// Log.i("debug", isFirstChecked
		// + "AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED");
		//
		// // openHongBao(event);
		// }

	}

	public void Lock() {
		// 判断 锁屏解锁
		KeyguardManager mKeyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
		Log.i("ddd", "mKeyguardManager.inKeyguardRestrictedInputMode() "
				+ mKeyguardManager.inKeyguardRestrictedInputMode());
		if (mKeyguardManager.inKeyguardRestrictedInputMode()) {

			wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
					| PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
			wakeLock.acquire();
			Log.i("debug", "------>acquire");
			// 初始化键盘锁，可以锁定或解开键盘锁
			mKeyguardLock = mKeyguardManager.newKeyguardLock("");
			// 禁用显示键盘锁定
			mKeyguardLock.disableKeyguard();
		}
		// 这句话可以判断屏幕是否 处于锁屏状态
	}

	/*
	 * @Override protected boolean onKeyEvent(KeyEvent event) { //return
	 * super.onKeyEvent(event); return true; }
	 */

	@Override
	public void onInterrupt() {
		if (mp != null) {
			mp.release();
		}
		Toast.makeText(this, "中断抢红包服务", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();
		Log.i("debug", "连接 抢红包服务");
		// 获取电源的服务
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		// 获取系统服务
		mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		Toast.makeText(this, "连接抢红包服务", Toast.LENGTH_SHORT).show();
	}

	private void sendNotifyEvent() {
		AccessibilityManager manager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
		if (!manager.isEnabled()) {
			return;
		}

		AccessibilityEvent event = AccessibilityEvent
				.obtain(AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED);
		event.setPackageName(WECHAT_PACKAGENAME);
		event.setClassName(Notification.class.getName());
		CharSequence tickerText = HONGBAO_TEXT_KEY;
		event.getText().add(tickerText);
		manager.sendAccessibilityEvent(event);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void setDelay() {
		AccessibilityServiceInfo info = getServiceInfo();
		info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
		info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
		int TO = (int) (Double.parseDouble(readSharpPreference("delay")) * 1000);
		info.notificationTimeout = TO;
		setServiceInfo(info);
		info.packageNames = new String[] { "com.tencent.mm" };
		setServiceInfo(info);
	}

	/** 打开通知栏消息 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void openNotify(AccessibilityEvent event) {
		if (event.getParcelableData() == null
				|| !(event.getParcelableData() instanceof Notification)) {
			return;
		}
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		ComponentName cn1 = am.getRunningTasks(1).get(0).topActivity;
		Log.d("debug", "cn1----" + cn1.toString());
		// 以下是精华，将微信的通知栏消息打开
		Notification notification = (Notification) event.getParcelableData();
		PendingIntent pendingIntent = notification.contentIntent;

		isFirstChecked = true;

		Log.i("debug", isFirstChecked + "openNotify.openNotify");
		try {
			pendingIntent.send();
			ComponentName cn2 = am.getRunningTasks(1).get(0).topActivity;
			Log.d("debug", "cn2----" + cn2.toString());
			if (cn1.equals(cn2)) {
				Log.d("debug", "cn1.equals(cn2)");
				checkKey2();
			}
		} catch (PendingIntent.CanceledException e) {
			e.printStackTrace();
		} finally {

		}
	}

	private void openHongBao(AccessibilityEvent event) {
		if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI"
				.equals(event.getClassName())) {
			// 点中了红包，下一步就是去拆红包
			if (isFirstChecked)
				checkKey1();
		} else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI"
				.equals(event.getClassName())) {
			// 拆完红包后看详细的纪录界面
			if (isFirstChecked) {
				isFirstChecked = false;
				isClicked = true;
				// 记录金额
				revordMoney();
				performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);

			}

			// nonething
		} else if ("com.tencent.mm.ui.LauncherUI".equals(event.getClassName())) {
			// 在聊天界面,去点中红包

			// performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
			if (isClicked) {
				isClicked = false;
				sendReply();
				performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
				if (wakeLock != null) {
					mKeyguardLock.reenableKeyguard();// 不加这句 屏幕就无法上锁了 放在这里
					// 可以避免锁屏时不回复
				} // 加了这句以后在锁屏状态下
				// 抢红包 抢完了 一句锁住

			}
			if (isFirstChecked)
				checkKey2();

		}
	}

	@SuppressLint("NewApi")
	private void checkKey1() {
		AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
		if (nodeInfo == null) {

			return;
		}
		List<AccessibilityNodeInfo> list = nodeInfo
				.findAccessibilityNodeInfosByText("拆红包");
		for (AccessibilityNodeInfo n : list) {
			n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
			return;
		}
		List<AccessibilityNodeInfo> list1 = nodeInfo
				.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b2c");
		for (AccessibilityNodeInfo n : list1) {
			n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
			return;
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void checkKey2() {
		AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
		if (mp != null) {
			mp.release();
		}
		if (nodeInfo == null) {
			Log.i("debug", "-->null:");
			return;
		}
		List<AccessibilityNodeInfo> list = nodeInfo
				.findAccessibilityNodeInfosByText("领取红包");
		if (list.isEmpty()) {
			Log.i("debug", "-list.isEmpty()");
			performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);

			// list =
			// nodeInfo.findAccessibilityNodeInfosByText(HONGBAO_TEXT_KEY);
			// for (AccessibilityNodeInfo n : list) {
			// Log.i("debug", "-->微信红包:" + n);
			// n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
			// break;
			// }既然为空 就是别人发送[微信红包] 这样的消息
		} else {
			Log.i("debug", "-else list.isEmpty()");

			// 最新的红包领起
			for (int i = list.size() - 1; i >= 0; i--) {
				AccessibilityNodeInfo parent = list.get(i).getParent();
				// Log.i("debug", "-->领取红包:" + parent);
				if (parent != null) {
					if (isFirstChecked) {
						// 我这里默认领 最后一个 一般是最后一个 你懂得 前面的都被领过了
						parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
					}

					break;
				}
			}
		}
	}

	private MediaPlayer mp;

	public String readSharpPreference(String key) {
		SharedPreferences preference = getSharedPreferences("wechatred",
				MODE_PRIVATE);

		String value = preference.getString(key, "");
		if (key.equals("delay")) {
			value = preference.getString(key, "0.1");
			if ("".equals(value)) {
				value = "0.1";
			}
		}
		if (key.equals("money")) {
			value = preference.getString(key, "0.00");
		}
		if (key.equals("count")) {
			value = preference.getString(key, "0");
		}
		if (key.equals("status")) {
			value = preference.getString(key, "1");
		}
		if (key.equals("reply")) {
			value = preference.getString(key, "");
			if (value.contains("#")) {
				String[] a = value.split("#");
				int length = a.length;
				int index = new Random().nextInt(length);
				value = a[index];
			}
		}
		return value;

	}

	public void writeSharpPreference(String key, String value) {

		SharedPreferences.Editor editor = getSharedPreferences("wechatred",
				MODE_PRIVATE).edit();
		editor.putString(key, value);
		editor.commit();

	}

	public void sendReply() {
		String reply = readSharpPreference("reply");
		if (!"".equals(reply)) {
			ClipboardManager clip = (ClipboardManager) this
					.getSystemService(Context.CLIPBOARD_SERVICE);
			clip.setText(reply);
			AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
			if (nodeInfo == null) {
				Log.i("ddd", "null ");
				return;
			}
			List<AccessibilityNodeInfo> list = nodeInfo
					.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/wh");
			if (list.size() <= 0) {
				list = nodeInfo
						.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/c39");

			}
			for (AccessibilityNodeInfo n : list) {
				n.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
				// n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
				n.performAction(AccessibilityNodeInfo.ACTION_PASTE);
			}
			List<AccessibilityNodeInfo> list1 = nodeInfo
					.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/wn");
			if (list1.size() <= 0) {
				list1 = nodeInfo
						.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/c3d");

			}
			for (AccessibilityNodeInfo n : list1) {
				n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
				Log.i("ddd", "setText ");
			}
			// Log.i("ddd",
			// "list " + (list == null) + "" + list.size()
			// + list.toString());
		}
	}

	public void revordMoney() {
		Log.i("ddd", "revordMoney ");
		String re = readSharpPreference("money");
		String count = readSharpPreference("count");
		int Co = Integer.parseInt(count);
		String much = "0.0";
		AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
		if (nodeInfo == null) {
			Log.i("ddd", "null ");
			return;
		}

		List<AccessibilityNodeInfo> list1 = nodeInfo
				.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b02");
		Log.i("ddd", "n.getText().toString().trim() " + list1.size());
		if (list1.size() <= 0) {
			list1 = nodeInfo
					.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b2o");
		}
		for (AccessibilityNodeInfo n : list1) {
			Log.i("ddd", "n.getText().toString().trim() "
					+ n.getText().toString().trim());
			much = n.getText().toString().trim();// 这个id检测出来跳过两次

			Co++;//
			Log.i("ddd", "much " + list1.size());
			;
			break;
		}
		try {
			Log.i("ddd", "Co " + Co);
			writeSharpPreference(
					"money",
					String.format("%.2f",
							(Double.parseDouble(much) + Double.parseDouble(re)))
							+ "");
			writeSharpPreference("count", Co + "");
		} catch (Exception e) {
			Log.i("ddd", "e  " + e.toString());
		}
	}

	public void play() {
		String url = readSharpPreference("ring");
		Uri uri = Uri.parse(url);

		if (mp != null) {
			mp.release();
		}

		try {

			mp = new MediaPlayer();

			mp.setDataSource(this, uri);

			mp.prepare();

			mp.start();

		} catch (Exception e) {

			e.printStackTrace();

			Toast.makeText(this, "e.printStackTrace(); " + e.toString(), 3)

					.show();

		}
	}
}
