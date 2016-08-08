package com.komok.wallpaperchanger;

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

public class LiveWallpaperResultActivity extends BaseActivity implements OnClickListener, OnStartDragListener {
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
			mButton.setText(String.format(getResources().getString(R.string.set_rendom_component), getResources().getString(R.string.wallpaper)));
		} else {
			mButton.setText(String.format(getResources().getString(R.string.set_component), getResources().getString(R.string.wallpaper)));
		}

		mButton.setOnClickListener(this);

		listView = (RecyclerView) findViewById(R.id.outputList);
		LiveWallpaperResultListAdapter adapter = new LiveWallpaperResultListAdapter(this, this, LiveWallpaperSelectionActivity.selectedTilesList);

		listView.setHasFixedSize(true);
		listView.setAdapter(adapter);
		listView.setLayoutManager(new LinearLayoutManager(this));

		ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
		mItemTouchHelper = new ItemTouchHelper(callback);
		mItemTouchHelper.attachToRecyclerView(listView);
	}

	@Override
	public void onClick(View v) {

		Map<String, String> selectedWallpapersMap = new LinkedHashMap<String, String>();

		for (Tile tile : LiveWallpaperSelectionActivity.selectedTilesList) {
			selectedWallpapersMap.put(tile.mLabel, tile.mIntent.toUri(0));
		}

		BaseHelper.saveWallpapersMap(selectedWallpapersMap, this, day);

		if (selectedWallpapersMap.size() == 0) {
			Intent intent = new Intent(this, LiveWallpaperSelectionActivity.class);

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

			BaseHelper.runDayActivity(BaseHelper.Components.LiveWallpaper, day, this);
		}

	}

	@Override
	public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
		mItemTouchHelper.startDrag(viewHolder);
	}

}
