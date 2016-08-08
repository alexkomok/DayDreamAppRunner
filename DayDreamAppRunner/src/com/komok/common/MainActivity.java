package com.komok.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.komok.appchanger.AppChangerActivity;
import com.komok.daydreamchanger.DayDreamChangerActivity;
import com.komok.wallpaperchanger.LiveWallpaperChangerActivity;
import com.komok.wallpaperchanger.R;

public class MainActivity extends BaseActivity implements OnClickListener {

	Button apps_button;
	Button wallpapers_button;
	Button dreams_button;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewsById();

		apps_button.setOnClickListener(this);
		wallpapers_button.setOnClickListener(this);
		dreams_button.setOnClickListener(this);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			String error = b.getString(BaseHelper.ERROR);
			if (error != null) {
				Toast.makeText(this, error, Toast.LENGTH_LONG).show();
			}
		}

	}

	private void findViewsById() {
		apps_button = (Button) findViewById(R.id.apps_button);
		wallpapers_button = (Button) findViewById(R.id.wallpapers_button);
		dreams_button = (Button) findViewById(R.id.dreams_button);
	}

	@Override
	public void onClick(View v) {

		Intent intent = new Intent();

		switch (v.getId()) {
		case R.id.apps_button:
			intent = new Intent(this, AppChangerActivity.class);
			break;
		case R.id.wallpapers_button:
			intent = new Intent(this, LiveWallpaperChangerActivity.class);
			break;
		case R.id.dreams_button:
			intent = new Intent(this, DayDreamChangerActivity.class);
			break;			

		}

		startActivity(intent);

	}
}
