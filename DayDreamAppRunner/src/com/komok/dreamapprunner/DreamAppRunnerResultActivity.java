package com.komok.dreamapprunner;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.komok.common.BaseActivity;
import com.komok.common.BaseHelper;
import com.komok.common.Tile;
import com.komok.itemtouchhelper.OnStartDragListener;
import com.komok.itemtouchhelper.SimpleItemTouchHelperCallback;
import com.komok.dreamapprunner.R;

public class DreamAppRunnerResultActivity extends BaseActivity implements OnClickListener, OnStartDragListener {
	String day;
	Button set_dream;
	RecyclerView listView;
	private ItemTouchHelper mItemTouchHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);


		Button mButton = (Button) findViewById(R.id.set_component);
		mButton.setText(getResources().getString(R.string.settings_save));

		mButton.setOnClickListener(this);

		listView = (RecyclerView) findViewById(R.id.outputList);
		DreamAppRunnerResultListAdapter adapter = new DreamAppRunnerResultListAdapter(this, this, DreamAppRunnerSettingsActivity.selectedTilesList);

		listView.setHasFixedSize(true);
		listView.setAdapter(adapter);
		listView.setLayoutManager(new LinearLayoutManager(this));

		ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
		mItemTouchHelper = new ItemTouchHelper(callback);
		mItemTouchHelper.attachToRecyclerView(listView);
	}

	@Override
	public void onClick(View v) {

		List<String> selectedList = new ArrayList<String>();

		for (Tile tile : DreamAppRunnerSettingsActivity.selectedTilesList) {
			selectedList.add(tile.mLabel);
		}

		BaseHelper.saveDreamChoice(selectedList, this);
		

		if (selectedList.size() == 0) {
			Intent intent = new Intent(this, DreamAppRunnerSettingsActivity.class);

			// Create a bundle object
			Bundle b = new Bundle();

			String error = getString(R.string.select_one_or_more);

			b.putString(BaseHelper.ERROR, error);

			// Add the bundle to the intent.
			intent.putExtras(b);

			startActivity(intent);
			finish();
		} else {
			Toast.makeText(this, getString(R.string.settings_saved), Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
		mItemTouchHelper.startDrag(viewHolder);
	}

}