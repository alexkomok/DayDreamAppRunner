package com.komok.dreamapprunner;

import java.util.ArrayList;
import java.util.List;

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
import com.komok.common.BaseHelper;
import com.komok.common.IItemChecked;
import com.komok.common.MainActivity;
import com.komok.common.Tile;
import com.komok.dreamapprunner.R;

public class DreamAppRunnerSettingsActivity extends BaseListActivity implements OnClickListener, IItemChecked {

	Button buttonNext;
	ListView listView;
	CheckBox checkBox;
	DreamAppRunnerListAdapter mAdapter;
	static List<String> selectedList;
	static List<Tile> selectedTilesList;
	String message;
	String error;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select);
		findViewsById();

		message = getString(R.string.select_one_or_more);
		buttonNext.setOnClickListener(this);
	}

	public void onStart() {
		super.onStart();

		mAdapter = new DreamAppRunnerListAdapter(this);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setAdapter(mAdapter);

		selectedList = BaseHelper.loadDreamChoice(this);
		Bundle b = getIntent().getExtras();
		error = b == null ? null : b.getString(BaseHelper.ERROR);

		if (BaseHelper.ERROR.equals(error)) {
			Toast.makeText(this, getString(R.string.error_update_list), Toast.LENGTH_LONG).show();
		} else if (error != null) {
			Toast.makeText(this, error, Toast.LENGTH_LONG).show();
		}

	}

	private void findViewsById() {
		listView = (ListView) findViewById(android.R.id.list);
		buttonNext = (Button) findViewById(R.id.button_next);
		checkBox = (CheckBox) findViewById(R.id.select_all);

	}

	public void onClick(View v) {
		SparseBooleanArray checked = listView.getCheckedItemPositions();
		selectedList = new ArrayList<String>();
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
					selectedList.add(tile.mLabel);
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

		BaseHelper.saveDreamChoice(selectedList, this);

		Intent intent = new Intent(this, DreamAppRunnerResultActivity.class);

		// start the ResultActivity
		startActivity(intent);
	}

	public void setItemChecked() {

		if (listView.getAdapter().getCount() == 0) {
			Intent intent = new Intent(this, MainActivity.class);

			// Create a bundle object
			Bundle b = new Bundle();
			b.putString(BaseHelper.ERROR, getString(R.string.select_one));

			// Add the bundle to the intent.
			intent.putExtras(b);

			// start the ResultActivity
			startActivity(intent);
		} else {

			for (int i = 0; i < listView.getAdapter().getCount(); i++) {
				Tile tile = (Tile) listView.getItemAtPosition(i);
				String label = tile.mLabel;
				if (selectedList.contains(label)) {
					listView.setItemChecked(i, true);
				} else {
					listView.setItemChecked(i, false);
				}
			}
		}
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
