package com.komok.daydream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;

import com.komok.common.AbstractBaseAdapter;
import com.komok.common.AbstractEnumerator;
import com.komok.common.BaseHelper;
import com.komok.common.Tile;

public class DayDreamListAdapter extends AbstractBaseAdapter<Tile> {

	public DayDreamListAdapter(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		List<String> list = BaseHelper.loadComponentsList(context);

		mTiles = new ArrayList<Tile>();
		new ComponentEnumerator(context, this).execute(list);
	}

	private class ComponentEnumerator extends AbstractEnumerator<String, Tile, DayDreamListAdapter> {

		public ComponentEnumerator(Context context, AbstractBaseAdapter<Tile> adapter) {
			super(context, adapter);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(List<String>... params) {

			List<String> list = params[0];

			Collections.sort(list);
			// Collections.reverse(list);

			Drawable wallpaperIcon = mContext.getResources().getDrawable(R.drawable.ic_menu_gallery);
			Drawable appIcon = mContext.getResources().getDrawable(R.drawable.ic_menu_more);
			Drawable daydreamIcon = mContext.getResources().getDrawable(R.drawable.ic_menu_view);

			for (String info : list) {
				if (info != null) {
					String[] splits = info.split(BaseHelper.splitter);
					Drawable icon = null;

					if (BaseHelper.Components.Application.name().equals(splits[0])) {
						icon = appIcon;
					} else if (BaseHelper.Components.DayDream.name().equals(splits[0])) {
						icon = daydreamIcon;
					} else if (BaseHelper.Components.LiveWallpaper.name().equals(splits[0])) {
						icon = wallpaperIcon;
					}

					Tile component = new Tile(icon, null, BaseHelper.getFormattedComponentName(splits[0], splits[1]));
					publishProgress(component);
				}
			}
			// Send a null object to show loading is finished
			publishProgress((Tile) null);

			return null;
		}

	}
}
