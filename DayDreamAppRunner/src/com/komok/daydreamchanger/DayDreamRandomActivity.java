package com.komok.daydreamchanger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.komok.common.ApplicationHolder;
import com.komok.common.BaseHelper;
import com.komok.common.BaseHelper.Weekday;

public class DayDreamRandomActivity extends AbstractDayDreamSetterActivity {

	@Override
	protected ApplicationHolder getDream() {
		Map<String, String> selectedDreamsMap = BaseHelper.loadDreamsMap(this, getDay().name());

		if (selectedDreamsMap.size() > 0) {
			Random random = new Random();
			List<String> keys = new ArrayList<String>(selectedDreamsMap.keySet());
			String label = keys.get(random.nextInt(keys.size()));
			String uri = selectedDreamsMap.get(label);

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
