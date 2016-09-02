package com.komok.dreamapprunner;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.service.dreams.DreamService;

import com.komok.common.BaseHelper;
import com.komok.daydreamchanger.AbstractDayDreamSetterActivity;

@SuppressLint("NewApi")
public class DreamAppRunnerService extends DreamService {
	List<String> selectedList;

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();

		setInteractive(false);

		setFullscreen(false);

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
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );

							intent.setClassName("com.android.systemui", "com.android.systemui.Somnambulator");
							getApplication().startActivity(intent);
						} else {
							Settings.Secure.putString(getContentResolver(), "screensaver_components", getApplicationInfo().packageName + "/"
									+ DreamAppRunnerService.class.getName());
						}

					}

				}, BaseHelper.getSystemTimeOut(this));

			} else if (BaseHelper.Components.LiveWallpaper.name().equals(parts[0])) {
				BaseHelper.runDayActivity(BaseHelper.Components.LiveWallpaper, parts[1], this);
			}

			BaseHelper.wakeup(this);

		}
		finish();
	}

}
