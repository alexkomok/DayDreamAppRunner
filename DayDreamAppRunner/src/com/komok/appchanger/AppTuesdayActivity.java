package com.komok.appchanger;

import com.komok.common.ApplicationHolder;
import com.komok.common.BaseHelper;
import com.komok.common.BaseHelper.Weekday;
import com.komok.appchanger.AbstractAppSetterActivity;


public class AppTuesdayActivity extends AbstractAppSetterActivity {

	@Override
	protected ApplicationHolder getApp() {
		return  BaseHelper.loadApp(this, getDay());
	}

	@Override
	protected Weekday getDay() {
		return BaseHelper.Weekday.Friday;
	}

}
