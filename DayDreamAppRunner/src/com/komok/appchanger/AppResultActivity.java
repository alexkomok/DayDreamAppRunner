package com.komok.appchanger;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.komok.common.BaseActivity;
import com.komok.common.BaseHelper;
import com.komok.common.Tile;
import com.komok.itemtouchhelper.OnStartDragListener;
import com.komok.itemtouchhelper.SimpleItemTouchHelperCallback;
import com.komok.wallpaperchanger.R;

public class AppResultActivity extends BaseActivity implements OnClickListener, OnStartDragListener {
	String day;
	Button set_wallpaper;
	RecyclerView listView;
	private ItemTouchHelper mItemTouchHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);

		Bundle b = getIntent().getExtras();
		day = b.getString(BaseHelper.DAY);

		Button mButton = (Button) findViewById(R.id.set_component);
		if (BaseHelper.Weekday.Random.name().equals(day)) {
			mButton.setText(String.format(getResources().getString(R.string.set_rendom_component), getResources().getString(R.string.app)));
		} else {
			mButton.setText(String.format(getResources().getString(R.string.set_component), getResources().getString(R.string.app)));
		}

		mButton.setOnClickListener(this);

		listView = (RecyclerView) findViewById(R.id.outputList);
		AppResultListAdapter adapter = new AppResultListAdapter(this, this, AppSelectionActivity.selectedTilesList);

		listView.setHasFixedSize(true);
		listView.setAdapter(adapter);
		listView.setLayoutManager(new LinearLayoutManager(this));

		ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
		mItemTouchHelper = new ItemTouchHelper(callback);
		mItemTouchHelper.attachToRecyclerView(listView);
	}

	@Override
	public void onClick(View v) {

		Map<String, String> selectedAppsMap = new LinkedHashMap<String, String>();

		for (Tile tile : AppSelectionActivity.selectedTilesList) {
			selectedAppsMap.put(tile.mLabel, tile.mIntent.toUri(0));
		}

		BaseHelper.saveAppsMap(selectedAppsMap, this, day);

		if (selectedAppsMap.size() == 0) {
			Intent intent = new Intent(this, AppSelectionActivity.class);

			// Create a bundle object
			Bundle b = new Bundle();
			b.putString(BaseHelper.DAY, day);

			String error = getString(R.string.select_one);

			if (BaseHelper.Weekday.Random.name().equals(day)) {
				error = getString(R.string.select_one_or_more);
			}

			b.putString(BaseHelper.ERROR, error);

			// Add the bundle to the intent.
			intent.putExtras(b);

			startActivity(intent);
			finish();
		} else {

			BaseHelper.runDayActivity(BaseHelper.Components.Application, day, this);
		}

	}

	@Override
	public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
		mItemTouchHelper.startDrag(viewHolder);
	}

}
