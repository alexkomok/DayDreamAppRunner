package com.komok.dreamapprunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.komok.appchanger.AppSelectionActivity;
import com.komok.common.AbstractBaseAdapter;
import com.komok.common.AbstractEnumerator;
import com.komok.common.BaseHelper;
import com.komok.common.Tile;
import com.komok.daydreamchanger.DayDreamSelectionActivity;
import com.komok.wallpaperchanger.LiveWallpaperSelectionActivity;

public class DreamAppRunnerListAdapter extends AbstractBaseAdapter<Tile> {
	public DreamAppRunnerListAdapter(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		List<String> list = BaseHelper.loadComponentsList(context);

		mTiles = new ArrayList<Tile>();
		new ComponentEnumerator(context, this).execute(list);
	}

	private class ComponentEnumerator extends AbstractEnumerator<String, Tile, DreamAppRunnerListAdapter> {

		public ComponentEnumerator(Context context, AbstractBaseAdapter<Tile> adapter) {
			super(context, adapter);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(List<String>... params) {

			List<String> list = params[0];

			Collections.sort(list);
			// Collections.reverse(list);

			Drawable wallpaperIcon = mContext.getResources().getDrawable(android.R.drawable.ic_menu_gallery);
			Drawable appIcon = mContext.getResources().getDrawable(android.R.drawable.ic_menu_more);
			Drawable daydreamIcon = mContext.getResources().getDrawable(android.R.drawable.ic_menu_view);

			for (String info : list) {
				if (info != null) {
					String[] splits = info.split(BaseHelper.splitter);
					Drawable icon = null;
					Class clazz = null;

					if (BaseHelper.Components.Application.name().equals(splits[0])) {
						icon = appIcon;
						clazz = AppSelectionActivity.class;
					} else if (BaseHelper.Components.DayDream.name().equals(splits[0])) {
						icon = daydreamIcon;
						clazz = DayDreamSelectionActivity.class;
					} else if (BaseHelper.Components.LiveWallpaper.name().equals(splits[0])) {
						icon = wallpaperIcon;
						clazz = LiveWallpaperSelectionActivity.class;
					}

					Tile component = new Tile(icon, null, BaseHelper.getFormattedComponentName(splits[0], splits[1]));
					Intent intent = new Intent(mContext, clazz);
					
					// Create a bundle object
					Bundle b = new Bundle();
					b.putString(BaseHelper.DAY, splits[1]);

					// Add the bundle to the intent.
					intent.putExtras(b);
					
					component.mIntent = intent;
					publishProgress(component);
				}
			}
			// Send a null object to show loading is finished
			publishProgress((Tile) null);

			return null;
		}

	}
}
