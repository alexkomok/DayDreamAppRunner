package com.komok.dreamapprunner;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.service.dreams.DreamService;
import android.service.wallpaper.WallpaperService;
import android.widget.Toast;

import com.komok.common.ApplicationHolder;
import com.komok.common.BaseHelper;
import com.komok.common.BaseHelper.Weekday;
import com.komok.daydreamchanger.AbstractDayDreamSetterActivity;
import com.komok.wallpaperchanger.AbstractLiveWallpaperSetterActivity;

@SuppressLint("NewApi")
public class DreamAppRunnerService extends DreamService {
	List<String> selectedList;

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		setInteractive(false);
		setFullscreen(false);

	}

	private void saveCurrentLWP() {
		WallpaperInfo wi = ((WallpaperManager) this.getSystemService("wallpaper")).getWallpaperInfo();
		if (wi != null) {
			Intent launchIntent = new Intent(WallpaperService.SERVICE_INTERFACE);
			launchIntent.setClassName(wi.getPackageName(), wi.getServiceName());
			launchIntent.setPackage(wi.getPackageName());
			Map<String, String> map = new LinkedHashMap<String, String>();
			map.put(BaseHelper.Weekday.Current.name(), launchIntent.toUri(0));
			BaseHelper.saveWallpapersMap(map, this, BaseHelper.Weekday.Current.name());
		} else {
			WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
			Drawable wallpaperDrawable = wallpaperManager.getDrawable();
			Bitmap bitmap = ((BitmapDrawable) wallpaperDrawable).getBitmap();
			BaseHelper.saveImage(this, bitmap, "wallpaperback", "jpg");
			Map<String, String> map = new LinkedHashMap<String, String>();
			BaseHelper.saveWallpapersMap(map, this, BaseHelper.Weekday.Current.name());
		}
	}

	private void restoreCurrentLWP() {
		if (BaseHelper.loadWallpapersMap(this, BaseHelper.Weekday.Current.name()).size() > 0) {
			//startActivity(new Intent(this, LiveWallpaperCurrentActivity.class));
		} else {
			Bitmap bitmap = BaseHelper.getImageBitmap(this, "wallpaperback", "jpg");
			WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
			try {
				myWallpaperManager.setBitmap(bitmap);
			} catch (Exception e) {
				Toast.makeText(this, "Error setting Wallpaper", Toast.LENGTH_SHORT).show();
			}

		}

	}

	private class LiveWallpaperCurrentActivity extends AbstractLiveWallpaperSetterActivity {

		@Override
		protected ApplicationHolder getLiveWallpaper() {
			return BaseHelper.loadLiveWallpaper(this, getDay());
		}

		@Override
		protected Weekday getDay() {
			return BaseHelper.Weekday.Current;
		}

	}

	@Override
	public void onDreamingStarted() {
		super.onDreamingStarted();
		selectedList = BaseHelper.loadDreamChoice(this);

		if (selectedList.size() == 0) {
			Intent intent = new Intent(this, DreamAppRunnerSettingsActivity.class);

			// Create a bundle object
			Bundle b = new Bundle();

			String error = getString(R.string.select_one_or_more);

			b.putString(BaseHelper.ERROR, error);

			// Add the bundle to the intent.
			intent.putExtras(b);

			startActivity(intent);
		} else {

			int savedPosition = BaseHelper.loadComponentListPosition(this);
			int size = selectedList.size();
			int nextPosition = 0;
			if (savedPosition < size) {
				nextPosition = savedPosition + 1 >= size ? 0 : savedPosition + 1;
			} else {
				savedPosition = size - 1;
			}

			BaseHelper.saveComponentListPosition(nextPosition, this);

			String component = selectedList.get(savedPosition);
			String[] parts = component.split(BaseHelper.splitter);
			
			restoreCurrentLWP();

			if (BaseHelper.Components.Application.name().equals(parts[0])) {
				BaseHelper.runDayActivity(BaseHelper.Components.Application, parts[1], this);
			} else if (BaseHelper.Components.DayDream.name().equals(parts[0])) {
				BaseHelper.runDayActivity(BaseHelper.Components.DayDream, parts[1], this);

				Handler mHandler = new Handler();
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						if (AbstractDayDreamSetterActivity.isDreamStarted) {
							BaseHelper.wakeup(getApplicationContext());
							Settings.Secure.putString(getContentResolver(), "screensaver_components", getApplicationInfo().packageName + "/"
									+ DreamAppRunnerService.class.getName());

							Intent intent = new Intent(Intent.ACTION_MAIN);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

							intent.setClassName("com.android.systemui", "com.android.systemui.Somnambulator");
							getApplication().startActivity(intent);
						} else {
							Settings.Secure.putString(getContentResolver(), "screensaver_components", getApplicationInfo().packageName + "/"
									+ DreamAppRunnerService.class.getName());
						}

					}

				}, BaseHelper.getSystemTimeOut(this));

			} else if (BaseHelper.Components.LiveWallpaper.name().equals(parts[0])) {
				saveCurrentLWP();
				BaseHelper.runDayActivity(BaseHelper.Components.LiveWallpaper, parts[1], this);
			}

			BaseHelper.wakeup(this);

		}
		finish();
	}

}
