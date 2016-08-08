package com.komok.daydreamchanger;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.komok.common.Tile;
import com.komok.itemtouchhelper.AbstractRecyclerListAdapter;
import com.komok.itemtouchhelper.ItemTouchHelperAdapter;
import com.komok.itemtouchhelper.OnStartDragListener;
import com.komok.dreamapprunner.R;

public class DayDreamResultListAdapter extends AbstractRecyclerListAdapter<Tile> implements ItemTouchHelperAdapter {

	public DayDreamResultListAdapter(Context context, OnStartDragListener dragStartListener, List<Tile> selectedTilesList) {
		super(context, dragStartListener, selectedTilesList);
	}

	@Override
	public void onBindViewHolder(final ItemViewHolder holder, int position) {

		final Tile tile = mTile.get(position);

		mViewOnClickListener = new OnClickListener() {
			public void onClick(View v) {

				if (tile.mSettingsActivity != null && tile.mIntent != null && tile.mIntent.getPackage() != null) {

					Intent intent = new Intent();
					intent.setComponent(new ComponentName(tile.mIntent.getPackage(), tile.mSettingsActivity));
					v.getContext().startActivity(intent);

				} else

					Toast.makeText(v.getContext(), String.format(v.getContext().getString(R.string.no_settings),v.getContext().getString(R.string.dream)), Toast.LENGTH_LONG).show();
			}
		};

		holder.textView.setText(tile.mLabel);
		holder.holderView.setOnClickListener(mViewOnClickListener);
		holder.imageView.setImageDrawable(tile.mThumbnail);

		// Start a drag whenever the handle view it touched
		holder.imageView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
					mDragStartListener.onStartDrag(holder);
				}
				return false;
			}
		});

	}
}