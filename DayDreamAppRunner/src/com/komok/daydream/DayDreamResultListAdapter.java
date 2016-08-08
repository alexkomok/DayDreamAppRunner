package com.komok.daydream;

import java.util.List;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;

import com.komok.common.Tile;
import com.komok.itemtouchhelper.AbstractRecyclerListAdapter;
import com.komok.itemtouchhelper.ItemTouchHelperAdapter;
import com.komok.itemtouchhelper.OnStartDragListener;

public class DayDreamResultListAdapter extends AbstractRecyclerListAdapter<Tile> implements ItemTouchHelperAdapter {

	public DayDreamResultListAdapter(Context context, OnStartDragListener dragStartListener, List<Tile> selectedTilesList) {
		super(context, dragStartListener, selectedTilesList);
	}

	@Override
	public void onBindViewHolder(final ItemViewHolder holder, int position) {

		final Tile tile = mTile.get(position);

		holder.textView.setText(tile.mLabel);
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