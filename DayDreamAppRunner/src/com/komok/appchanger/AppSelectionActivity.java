package com.komok.appchanger;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.komok.common.BaseListActivity;
import com.komok.common.BaseHelper;
import com.komok.common.IItemChecked;
import com.komok.common.Tile;
import com.komok.wallpaperchanger.R;

public class AppSelectionActivity extends BaseListActivity implements OnClickListener, IItemChecked {

	Button button;
	ListView listView;
	CheckBox checkBox;
	RadioGroup appsGroup;
	RadioButton radioButtonAll, radioButtonSys, radioButtonUser;
	AppListAdapter mAdapter;
	String day;
	Map<String, String> selectedAppsMap;
	String message;
	String error;
	static List<Tile> selectedTilesList;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select);
		findViewsById();

		Bundle b = getIntent().getExtras();
		String apps = b.getString(BaseHelper.APPS);

		if (BaseHelper.Apps.All.name().equals(apps)) {
			mAdapter = new AppListAdapter(this, BaseHelper.Apps.All);
			radioButtonAll.setChecked(true);
		} else if (BaseHelper.Apps.Sys.name().equals(apps)) {
			mAdapter = new AppListAdapter(this, BaseHelper.Apps.Sys);
			radioButtonSys.setChecked(true);
		} else if (BaseHelper.Apps.User.name().equals(apps)) {
			mAdapter = new AppListAdapter(this, BaseHelper.Apps.User);
			radioButtonUser.setChecked(true);
		} else {
			mAdapter = new AppListAdapter(this, null);
		}

		listView.setAdapter(mAdapter);
		button.setOnClickListener(this);
		appsGroup.setVisibility(View.VISIBLE);
	}

	public void onRadioButtonClicked(View view) {
		// Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();

		// Check which radio button was clicked
		switch (view.getId()) {
		case R.id.radioAll:
			if (checked) {
				Bundle b = new Bundle();
				b.putString(BaseHelper.APPS, BaseHelper.Apps.All.name());
				Intent intent = getIntent();
				intent.putExtras(b);
				finish();
				startActivity(intent);
			}
			break;
		case R.id.radioSys:
			if (checked) {
				Bundle b = new Bundle();
				b.putString(BaseHelper.APPS, BaseHelper.Apps.Sys.name());
				Intent intent = getIntent();
				intent.putExtras(b);
				finish();
				startActivity(intent);
			}
			break;

		case R.id.radioUser:
			if (checked) {
				Bundle b = new Bundle();
				b.putString(BaseHelper.APPS, BaseHelper.Apps.User.name());
				Intent intent = getIntent();
				intent.putExtras(b);
				finish();
				startActivity(intent);
			}
			break;
		}
	}

	public void onStart() {
		super.onStart();
		Bundle b = getIntent().getExtras();
		day = b.getString(BaseHelper.DAY);
		error = b.getString(BaseHelper.ERROR);
		checkBox.setChecked(false);

		selectedAppsMap = BaseHelper.loadAppsMap(this, day);

		if (BaseHelper.Weekday.Random.name().equals(day) || BaseHelper.Weekday.List.name().equals(day)) {
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			checkBox.setVisibility(View.VISIBLE);
			message = getString(R.string.select_one_or_more);
		} else {
			listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			checkBox.setVisibility(View.GONE);
			message = getString(R.string.select_one);
		}

		if (BaseHelper.ERROR.equals(error)) {
			Toast.makeText(this, getString(R.string.error_update_list), Toast.LENGTH_LONG).show();
		} else if (error != null) {
			Toast.makeText(this, error, Toast.LENGTH_LONG).show();
		}

		setItemChecked();

	}

	public void setItemChecked() {

		for (int i = 0; i < listView.getAdapter().getCount(); i++) {
			Tile tile = (Tile) listView.getItemAtPosition(i);
			String label = tile.mLabel;
			if (selectedAppsMap.containsKey(label)) {
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
		appsGroup = (RadioGroup) findViewById(R.id.radioApps);
		radioButtonAll = (RadioButton) findViewById(R.id.radioAll);
		radioButtonSys = (RadioButton) findViewById(R.id.radioSys);
		radioButtonUser = (RadioButton) findViewById(R.id.radioUser);
	}

	public void onClick(View v) {
		SparseBooleanArray checked = listView.getCheckedItemPositions();
		selectedAppsMap = new LinkedHashMap<String, String>();
		selectedTilesList = new ArrayList<Tile>();

		boolean isAnyChecked = false;
		for (int i = 0; i < checked.size(); i++) {
			// Item position in adapter
			int position = checked.keyAt(i);

			if (checked.valueAt(i)) {
				isAnyChecked = true;
				Tile tile = (Tile) mAdapter.getItem(position);
				selectedTilesList.add(tile);
				String label = tile.mLabel;
				try {
					selectedAppsMap.put(label, tile.mIntent.toUri(0));
				} catch (IllegalArgumentException e) {
					Toast.makeText(this, "Duplicated name. Please, deselect: " + label, Toast.LENGTH_LONG).show();
					return;
				}
			}
		}

		if (!isAnyChecked) {
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			return;
		}

		BaseHelper.saveAppsMap(selectedAppsMap, this, day);

		Intent intent = new Intent(this, AppResultActivity.class);

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
