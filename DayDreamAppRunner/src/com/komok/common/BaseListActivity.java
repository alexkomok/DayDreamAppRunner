package com.komok.common;

import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.komok.dreamapprunner.DreamAppRunnerSettingsActivity;
import com.komok.dreamapprunner.R;

public class BaseListActivity extends ListActivity {
	
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
	        	intent = new Intent(this, DreamAppRunnerSettingsActivity.class);
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
