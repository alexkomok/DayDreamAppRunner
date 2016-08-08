package com.komok.daydreamchanger;

import com.komok.common.AbstractChangerActivity;


public class DayDreamChangerActivity extends AbstractChangerActivity<DayDreamSelectionActivity> {

	@Override
	public Class<DayDreamSelectionActivity> getSelectionActivityClass() {
		return DayDreamSelectionActivity.class;
	}
	

}
