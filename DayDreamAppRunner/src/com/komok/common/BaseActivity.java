package com.komok.common;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.komok.daydream.DayDreamSettingsActivity;
import com.komok.wallpaperchanger.R;

public class BaseActivity extends Activity {
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.dream_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.settings:
	        	intent = new Intent(this, DayDreamSettingsActivity.class);
	        	startActivity(intent);
	            return true;
	        case R.id.create:
	        	intent = new Intent(this, MainActivity.class);
	        	startActivity(intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	    
	}

}
