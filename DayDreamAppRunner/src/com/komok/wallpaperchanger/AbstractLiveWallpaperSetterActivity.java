package com.komok.wallpaperchanger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;

import com.komok.common.ApplicationHolder;
import com.komok.common.BaseHelper;
import com.komok.common.ExceptionHandler;
import com.komok.dreamapprunner.R;

abstract class AbstractLiveWallpaperSetterActivity extends Activity {

	private static final String TAG = "AbstractLiveWallpaperSetterActivity";

	protected abstract ApplicationHolder getLiveWallpaper();

	protected abstract BaseHelper.Weekday getDay();

	Method method;
	Object objIWallpaperManager;
	boolean isPermissionGranted;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		isPermissionGranted = BaseHelper.checkSetWallpaperComponentPermission(this);

		if (isPermissionGranted) {

			WallpaperManager manager = WallpaperManager.getInstance(this);

			try {
				method = WallpaperManager.class.getMethod("getIWallpaperManager", null);
				objIWallpaperManager = method.invoke(manager, null);
				Class[] param = new Class[1];
				param[0] = ComponentName.class;
				method = objIWallpaperManager.getClass().getMethod("setWallpaperComponent", param);

			} catch (NoSuchMethodException e) {
				Log.e(TAG, "Error onCreate", e);
				ExceptionHandler.caughtException(e, this);
			} catch (IllegalAccessException e) {
				Log.e(TAG, "Error onCreate", e);
				ExceptionHandler.caughtException(e, this);
			} catch (InvocationTargetException e) {
				Log.e(TAG, "Error onCreate", e);
				ExceptionHandler.caughtException(e, this);
			}
		}

	}


	@Override
	protected void onStart() {
		super.onStart();
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		ApplicationHolder wallpaper = getLiveWallpaper();

		if (wallpaper == null) {
			ExceptionHandler.caughtException(new Exception(getString(R.string.error_update_list) + " for: " + getDay().name()), this);
			return;
		}

		Intent intent = new Intent();
		boolean isSuccess = false;
		String error = BaseHelper.ERROR;

		try {
			intent = Intent.parseUri(wallpaper.getUri(), 0);
		} catch (URISyntaxException e) {
			Log.e(TAG, "Failed to set wallpaper: " + e);
			ExceptionHandler.caughtException(e, this);
		}

		String packageName = intent.getComponent().getPackageName();
		String className = intent.getComponent().getClassName();

		if (!isPermissionGranted && Build.VERSION.SDK_INT > 15) {

			intent = new Intent();
			intent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
			intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(packageName, className));

			isSuccess = true;
			this.startActivityForResult(intent, 0);
		} else if (isPermissionGranted) {
			try {
				intent = new Intent(WallpaperService.SERVICE_INTERFACE);
				intent.setClassName(packageName, className);
				method.invoke(objIWallpaperManager, intent.getComponent());
				isSuccess = true;

				Handler mHandler = new Handler();
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						Intent homeIntent = new Intent(Intent.ACTION_MAIN);
						homeIntent.addCategory(Intent.CATEGORY_HOME);
						homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(homeIntent);
					}

				}, 200L);

				// }
			} catch (IllegalAccessException e) {
				Log.e(TAG, "Failed to set wallpaper: " + e);
				ExceptionHandler.caughtException(e, this);
			} catch (InvocationTargetException e) {
				Log.e(TAG, "Failed to set wallpaper: " + e);
				ExceptionHandler.caughtException(e, this);
			}
		} else {
			error = getString(R.string.no_permission);
		}

		if (!isSuccess) {

			intent = new Intent(this, LiveWallpaperSelectionActivity.class);

			// Create a bundle object
			Bundle b = new Bundle();
			b.putString(BaseHelper.DAY, getDay().name());
			b.putString(BaseHelper.ERROR, error);

			// Add the bundle to the intent.
			intent.putExtras(b);

			startActivity(intent);

		}

	}

}
