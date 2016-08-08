package com.komok.appchanger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.komok.common.ApplicationHolder;
import com.komok.common.BaseHelper;
import com.komok.common.BaseHelper.Weekday;
import com.komok.appchanger.AbstractAppSetterActivity;

public class AppRandomActivity extends AbstractAppSetterActivity {

	@Override
	protected ApplicationHolder getApp() {
		Map<String, String> selectedAppsMap = BaseHelper.loadAppsMap(this, getDay().name());

		if (selectedAppsMap.size() > 0) {
			Random random = new Random();
			List<String> keys = new ArrayList<String>(selectedAppsMap.keySet());
			String label = keys.get(random.nextInt(keys.size()));
			String uri = selectedAppsMap.get(label);

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
