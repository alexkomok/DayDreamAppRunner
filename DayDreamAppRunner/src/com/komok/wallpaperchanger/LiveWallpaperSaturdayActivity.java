package com.komok.wallpaperchanger;

import com.komok.common.ApplicationHolder;
import com.komok.common.BaseHelper;
import com.komok.common.BaseHelper.Weekday;


public class LiveWallpaperSaturdayActivity extends AbstractLiveWallpaperSetterActivity {

	@Override
	protected ApplicationHolder getLiveWallpaper() {
		return  BaseHelper.loadLiveWallpaper(this, getDay());
	}

	@Override
	protected Weekday getDay() {
		return BaseHelper.Weekday.Saturday;
	}

}
