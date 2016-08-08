package com.komok.daydreamchanger;

import java.net.URISyntaxException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

	private static final String TAG = "AbstractAppSetterActivity";

	protected abstract ApplicationHolder getDream();

	protected abstract BaseHelper.Weekday getDay();

	boolean isPermissionGranted;
	String error;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		isPermissionGranted = BaseHelper.checkSetDayDreamComponentPermission(this);
		error = BaseHelper.ERROR;

	}

	@Override
	protected void onStart() {
		super.onStart();

		final Context mContext = this;
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

			Settings.Secure.putString(this.getContentResolver(), "screensaver_components", intent.getComponent().flattenToString());

			intent = new Intent(Intent.ACTION_MAIN);

			// Somnabulator is undocumented--may be removed in a future
			// version...
			intent.setClassName("com.android.systemui", "com.android.systemui.Somnambulator");

			startActivity(intent);

			Handler mHandler = new Handler();
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					Settings.Secure.putString(mContext.getContentResolver(), "screensaver_components", mContext.getApplicationInfo().packageName
							+ "/" + DreamAppRunnerService.class.getName());
				}

			}, 300L);
		}

	}

}
