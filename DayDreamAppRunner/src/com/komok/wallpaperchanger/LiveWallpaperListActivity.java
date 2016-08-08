package com.komok.wallpaperchanger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.komok.common.ApplicationHolder;
import com.komok.common.BaseHelper;
import com.komok.common.BaseHelper.Weekday;

public class LiveWallpaperListActivity extends AbstractLiveWallpaperSetterActivity {

	@Override
	protected ApplicationHolder getLiveWallpaper() {
		Map<String, String> selectedWallpapersMap = BaseHelper.loadWallpapersMap(this, getDay().name());
		int size = selectedWallpapersMap.size();
		int savedPosition = BaseHelper.loadWallpaperListPosition(this);

		if (size > 0) {
			int nextPosition = 0;
			if (savedPosition < size) {
				nextPosition = savedPosition + 1 >= size ? 0 : savedPosition + 1;
			} else {
				savedPosition = size - 1;
			}
			List<String> keys = new ArrayList<String>(selectedWallpapersMap.keySet());
			Collections.reverse(keys);
			String label = keys.get(savedPosition);
			String uri = selectedWallpapersMap.get(label);
			BaseHelper.saveWallpaperListPosition(nextPosition, this);
			return new ApplicationHolder(label, uri);

		}
		return null;

	}

	@Override
	protected Weekday getDay() {
		return BaseHelper.Weekday.List;
	}

}
