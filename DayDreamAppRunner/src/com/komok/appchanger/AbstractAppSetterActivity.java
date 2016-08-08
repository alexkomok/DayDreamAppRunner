package com.komok.appchanger;

import java.net.URISyntaxException;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.komok.common.ApplicationHolder;
import com.komok.common.BaseHelper;
import com.komok.common.ExceptionHandler;
import com.komok.dreamapprunner.R;

abstract public class AbstractAppSetterActivity extends Activity {

	private static final String TAG = "AbstractAppSetterActivity";

	protected abstract ApplicationHolder getApp();

	protected abstract BaseHelper.Weekday getDay();

	@Override
	protected void onStart() {
		super.onStart();
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		ApplicationHolder app = getApp();

		if (app == null) {
			ExceptionHandler.caughtException(new Exception(getString(R.string.error_update_list) + " for: " + getDay().name()), this);
			return;
		}

		Intent intent = new Intent();

		try {
			intent = Intent.parseUri(app.getUri(), 0);
		} catch (URISyntaxException e) {
			Log.e(TAG, "Failed to set app: " + e);
			ExceptionHandler.caughtException(e, this);
		}
		startActivity(intent);

	}

}
