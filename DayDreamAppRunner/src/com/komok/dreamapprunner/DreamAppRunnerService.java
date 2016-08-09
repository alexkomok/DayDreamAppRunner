package com.komok.dreamapprunner;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.service.dreams.DreamService;

import com.komok.common.BaseHelper;

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
			finish();
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
			
			if(savedPosition == nextPosition){
				return;
			}
			
			String component = selectedList.get(savedPosition);
			String[] parts = component.split(BaseHelper.splitter);

			if (BaseHelper.Components.Application.name().equals(parts[0])) {
				BaseHelper.runDayActivity(BaseHelper.Components.Application, parts[1], this);

			} else if (BaseHelper.Components.DayDream.name().equals(parts[0])) {
				BaseHelper.runDayActivity(BaseHelper.Components.DayDream, parts[1], this);
			}
			if (BaseHelper.Components.LiveWallpaper.name().equals(parts[0])) {
				BaseHelper.runDayActivity(BaseHelper.Components.LiveWallpaper, parts[1], this);
			}

			PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
			WakeLock wakeLock = pm.newWakeLock(
					(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
			wakeLock.acquire();
			wakeLock.release();
		}

	}

}
