package com.komok.wallpaperchanger;

import com.komok.common.AbstractChangerActivity;


public class LiveWallpaperChangerActivity extends AbstractChangerActivity<LiveWallpaperSelectionActivity> {

	@Override
	public Class<LiveWallpaperSelectionActivity> getSelectionActivityClass() {
		return LiveWallpaperSelectionActivity.class;
	}
	

}
