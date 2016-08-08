package com.komok.wallpaperchanger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.komok.common.BaseListActivity;
import com.komok.common.AbstractBaseAdapter;
import com.komok.common.BaseHelper;
import com.komok.common.IItemChecked;
import com.komok.common.Tile;

public class LiveWallpaperSelectionActivity extends BaseListActivity implements OnClickListener, IItemChecked {

	Button button;
	ListView listView;
	CheckBox checkBox;
	AbstractBaseAdapter<Tile> mAdapter;
	String day;
	Map<String, String> selectedWallpapersMap;
	String message;
	String error;
	static List<Tile> selectedTilesList;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select);
		findViewsById();

		mAdapter = new LiveWallpaperListAdapter(this);

		listView.setAdapter(mAdapter);
		button.setOnClickListener(this);
	}

	public void onStart() {
		super.onStart();
		Bundle b = getIntent().getExtras();
		day = b.getString(BaseHelper.DAY);
		error = b.getString(BaseHelper.ERROR);
		checkBox.setChecked(false);
		selectedWallpapersMap = BaseHelper.loadWallpapersMap(this, day);

		if (BaseHelper.Weekday.Random.name().equals(day) || BaseHelper.Weekday.List.name().equals(day)) {
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			checkBox.setVisibility(View.VISIBLE);
			message = getString(R.string.select_one_or_more);
		} else {
			listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			checkBox.setVisibility(View.GONE);
			message = getString(R.string.select_one);
		}
		
		if(BaseHelper.ERROR.equals(error)){
			Toast.makeText(this, getString(R.string.error_update_list), Toast.LENGTH_LONG).show();
		} else if (error != null){
			Toast.makeText(this, error, Toast.LENGTH_LONG).show();
		}
		
		setItemChecked();

	}

	public void setItemChecked() {

		for (int i = 0; i < listView.getAdapter().getCount(); i++) {
			Tile tile = (Tile) listView.getItemAtPosition(i);
			if (selectedWallpapersMap.containsKey(tile.mLabel)) {
				listView.setItemChecked(i, true);
			} else {
				listView.setItemChecked(i, false);
			}
		}
	}

	private void findViewsById() {
		listView = (ListView) findViewById(android.R.id.list);
		button = (Button) findViewById(R.id.button_next);
		checkBox = (CheckBox) findViewById(R.id.select_all);
	}

	public void onClick(View v) {
		SparseBooleanArray checked = listView.getCheckedItemPositions();
		selectedWallpapersMap = new LinkedHashMap<String, String>();
		selectedTilesList = new ArrayList<Tile>();

		boolean isAnyChecked = false;
		for (int i = 0; i < checked.size(); i++) {
			// Item position in adapter
			int position = checked.keyAt(i);
			
			if (checked.valueAt(i)) {
				isAnyChecked = true;
				Tile tile = (Tile) mAdapter.getItem(position);
				selectedTilesList.add(tile);
				selectedWallpapersMap.put(tile.mLabel, tile.mIntent.toUri(0));
			}
		}
		
		if(!isAnyChecked) {
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			return;
		}

		BaseHelper.saveWallpapersMap(selectedWallpapersMap, this, day);

		Intent intent = new Intent(this, LiveWallpaperResultActivity.class);

		// Create a bundle object
		Bundle b = new Bundle();
		b.putString(BaseHelper.DAY, day);

		// Add the bundle to the intent.
		intent.putExtras(b);

		// start the ResultActivity
		startActivity(intent);
	}

	public void onCheckboxClicked(View view) {
		// Is the view now checked?
		boolean checked = ((CheckBox) view).isChecked();

		// Check which checkbox was clicked
		switch (view.getId()) {
		case R.id.select_all:
			if (checked) {
				for (int i = 0; i < listView.getAdapter().getCount(); i++) {
					listView.setItemChecked(i, true);
				}
			} else {
				for (int i = 0; i < listView.getAdapter().getCount(); i++) {
					listView.setItemChecked(i, false);
				}
			}
			break;

		}
	}

}
