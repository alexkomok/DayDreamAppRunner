package com.komok.appchanger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.komok.common.ApplicationHolder;
import com.komok.common.BaseHelper;
import com.komok.common.BaseHelper.Weekday;

public class AppListActivity extends AbstractAppSetterActivity {

	@Override
	protected ApplicationHolder getApp() {
		Map<String, String> selectedAppsMap = BaseHelper.loadAppsMap(this, getDay().name());
		int size = selectedAppsMap.size();
		int savedPosition = BaseHelper.loadAppListPosition(this);

		if (size > 0) {
			int nextPosition = 0;
			if (savedPosition < size) {
				nextPosition = savedPosition + 1 >= size ? 0 : savedPosition + 1;
			} else {
				savedPosition = size - 1;
			}
			List<String> keys = new ArrayList<String>(selectedAppsMap.keySet());
			Collections.reverse(keys);
			String label = keys.get(savedPosition);
			String uri = selectedAppsMap.get(label);
			BaseHelper.saveAppListPosition(nextPosition, this);
			return new ApplicationHolder(label, uri);

		}
		return null;

	}

	@Override
	protected Weekday getDay() {
		return BaseHelper.Weekday.List;
	}

}
