package com.komok.wallpaperchanger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.komok.common.ApplicationHolder;
import com.komok.common.BaseHelper;
import com.komok.common.BaseHelper.Weekday;

public class LiveWallpaperRandomActivity extends AbstractLiveWallpaperSetterActivity {

	@Override
	protected ApplicationHolder getLiveWallpaper() {
		Map<String, String> selectedWallpapersMap = BaseHelper.loadWallpapersMap(this, getDay().name());

		if (selectedWallpapersMap.size() > 0) {
			Random random = new Random();
			List<String> keys = new ArrayList<String>(selectedWallpapersMap.keySet());
			String label = keys.get(random.nextInt(keys.size()));
			String uri = selectedWallpapersMap.get(label);

			return new ApplicationHolder(label, uri);
		} else {
			return null;
		}
	}

	@Override
	protected Weekday getDay() {
		return BaseHelper.Weekday.Random;
	}

}
