package com.komok.common;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.komok.wallpaperchanger.R;

public abstract class AbstractBaseAdapter<T extends Tile> extends BaseAdapter implements ListAdapter {
	
	public List<T> mTiles;
	protected LayoutInflater mInflater;
	
	public long getItemId(int position) {
		return position;
	}
	
	public int getCount() {
		if (mTiles == null) {
			return 0;
		}
		return mTiles.size();
	}

	public Tile getItem(int position) {
		return mTiles.get(position);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;

		if (convertView == null) {
			view = mInflater.inflate(R.layout.tile, parent, false);
		} else {
			view = convertView;
		}

		Tile tile = (Tile) mTiles.get(position);
		ImageView image = (ImageView) view.findViewById(R.id.tile_image);
		image.setImageDrawable(tile.mThumbnail);

		TextView label = (TextView) view.findViewById(R.id.tile_label);
		label.setText(tile.mLabel);

		return view;
	}

}
