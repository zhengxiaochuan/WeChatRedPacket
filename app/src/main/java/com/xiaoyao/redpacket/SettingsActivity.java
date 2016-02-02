package com.xiaoyao.redpacket;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.URI;

public class SettingsActivity extends Activity {
    /** 设置红包铃声 */
	private TextView mTvSetRingTitle, mTvSetRing;
	private EditText delay, reply;

	private SharedPreferences preference;
	public RingtoneManager rm;

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
		setContentView(R.layout.activity_settings);
		preference = getSharedPreferences("wechatred", MODE_PRIVATE);
		((TextView) findViewById(R.id.reg_back_bt))
				.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        SettingsActivity.this.finish();
                    }
                });

        mTvSetRingTitle = ((TextView) findViewById(R.id.tv_set_ring_title));
        mTvSetRingTitle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                setRedRing();
            }
        });
        mTvSetRing = ((TextView) findViewById(R.id.tv_set_ring));
        mTvSetRing.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                setRedRing();
            }
        });
        mTvSetRing.setText(readSharpPreference("ring"));
		delay = ((EditText) findViewById(R.id.delay));
		delay.setText(readSharpPreference("delay"));
		reply = ((EditText) findViewById(R.id.reply));
		String r = readSharpPreference("reply");
		if (!"".equals(r)) {
			reply.setText(r);
		}

		setListener();
	}

	public void setListener() {
		delay.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {
				// TODO Auto-generated method stub
				// Toast.makeText(SettingsActivity.this, "onTextChanged", 3).show();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
										  int arg2, int arg3) {
				// TODO Auto-generated method stub
				// Toast.makeText(SettingsActivity.this, "beforeTextChanged ", 3)
				// .show();
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				// Toast.makeText(SettingsActivity.this, "afterTextChanged",
				// 3).show();
				writeSharpPreference("delay", delay.getText().toString().trim());

			}
		});
		delay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// TODO Auto-generated method stub
				Toast.makeText(SettingsActivity.this, "setOnClickListener", 3)
						.show();

			}
		});
		reply.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
										  int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				writeSharpPreference("reply", reply.getText().toString().trim());
			}
		});

	}

	/**
	 * 设置红包铃声
	 */
	public void setRedRing() {

		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		// 设置铃声类型和title
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
				RingtoneManager.TYPE_NOTIFICATION);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "设置通知铃声");
		// 当设置完成之后返回到当前的Activity
		startActivityForResult(intent, 1);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	public void writeSharpPreference(String key, String value) {

		SharedPreferences.Editor editor = preference.edit();
		editor.putString(key, value);
		editor.commit();

	}

	public String readSharpPreference(String key) {

		String value = preference.getString(key, "");
		if (key.equals("ring") && "".equals(key)) {
			value = "点击设置属于我的红包铃声";

		}
		if (key.equals("ring") && !"".equals(key)) {
			// 比如此时的uri为 file:///mnt/sdcard/external_sd/test.txt
			if (!value.startsWith("content")) {
				try {
					File file = new File(new URI(value));
					value = file.getName();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					value = "点击设置属于我的红包铃声";
				}
			}
		}
		if (key.equals("delay")) {
			{
				value = preference.getString(key, "0.1");

			}
		}
		return value;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
			case 1:
				try {
					// 得到我们选择的铃声
					Uri pickedUri = data
							.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
					// 将我们选择的铃声设置成为默认
					if (pickedUri != null) {

						// Toast.makeText(this, pickedUri.toString(), 3).show();

						writeSharpPreference("ring",
								Uri.decode(pickedUri.toString()));

                        mTvSetRing.setText(readSharpPreference("ring"));
					} else {
						writeSharpPreference("ring", "");
                        mTvSetRing.setText(readSharpPreference("ring"));
					}
					Toast.makeText(this, "设置成功!", Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					Toast.makeText(this, "e " + e.toString(), Toast.LENGTH_SHORT).show();

				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
