package com.komok.daydreamchanger;

import java.net.URISyntaxException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.komok.common.ApplicationHolder;
import com.komok.common.BaseHelper;
import com.komok.common.ExceptionHandler;
import com.komok.dreamapprunner.DreamAppRunnerService;
import com.komok.dreamapprunner.DreamAppRunnerSettingsActivity;
import com.komok.dreamapprunner.R;

abstract public class AbstractDayDreamSetterActivity extends Activity {

	private static final String TAG = AbstractDayDreamSetterActivity.class.toString();

	protected abstract ApplicationHolder getDream();

	protected abstract BaseHelper.Weekday getDay();

	public static boolean isDreamStarted;

	private final long delay = 1000L;

	boolean isPermissionGranted;
	String error;
	private BroadcastReceiver receiver;
	private IntentFilter filter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		isPermissionGranted = BaseHelper.checkSetDayDreamComponentPermission(this);
		error = BaseHelper.ERROR;

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, TAG + " received broacast intent: " + intent);
				if (intent.getAction().equals(Intent.ACTION_DREAMING_STOPPED)) {
					Log.d(TAG, "received dream stoped");
					isDreamStarted = false;
					finish();
				} else if (intent.getAction().equals(Intent.ACTION_DREAMING_STARTED)) {
					Log.d(TAG, "received dream started");
					isDreamStarted = true;
				}
			}
		};

		filter = new IntentFilter(Intent.ACTION_DREAMING_STOPPED);
		filter.addAction(Intent.ACTION_DREAMING_STARTED);
		this.registerReceiver(receiver, filter);

	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			this.unregisterReceiver(receiver);
		} catch (final Exception exception) {
			// The receiver was not registered.
			// There is nothing to do in that case.
			// Everything is fine.
		}
	}
	

	@Override
	protected void onStart() {
		super.onStart();

		ApplicationHolder app = getDream();

		if (app == null) {
			ExceptionHandler.caughtException(new Exception(getString(R.string.error_update_list) + " for: " + getDay().name()), this);
			return;
		}

		Intent intent = new Intent();

		if (!isPermissionGranted) {
			error = getString(R.string.no_permission);
			intent = new Intent(this, DreamAppRunnerSettingsActivity.class);

			// Create a bundle object
			Bundle b = new Bundle();
			b.putString(BaseHelper.DAY, getDay().name());
			b.putString(BaseHelper.ERROR, error);

			// Add the bundle to the intent.
			intent.putExtras(b);

			startActivity(intent);
		} else {

			try {
				intent = Intent.parseUri(app.getUri(), 0);
			} catch (URISyntaxException e) {
				Log.e(TAG, "Failed to set app: " + e);
				ExceptionHandler.caughtException(e, this);
			}

			Settings.Secure.putString(getContentResolver(), "screensaver_components", intent.getComponent().flattenToString());

			final Intent dreamIntent = new Intent(Intent.ACTION_MAIN);

			dreamIntent.setClassName("com.android.systemui", "com.android.systemui.Somnambulator");
			dreamIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			startActivity(dreamIntent);

			final Handler mHandler = new Handler();

			Runnable runnable = new Runnable() {

				long timeLeft = BaseHelper.getSystemTimeOut(getApplicationContext());

				@Override
				public void run() {
					if (isDreamStarted || (!isDreamStarted && timeLeft < 0)) {
						Settings.Secure.putString(getContentResolver(), "screensaver_components", getApplicationInfo().packageName + "/"
								+ DreamAppRunnerService.class.getName());
					} else {
						startActivity(dreamIntent);
						timeLeft = timeLeft - delay;
						mHandler.postDelayed(this, delay);
					}
					Log.d(TAG, "received dream started=" + isDreamStarted + ", timeLeft=" + timeLeft);
				}
			};

			mHandler.postDelayed(runnable, delay);

		}

	}

}
