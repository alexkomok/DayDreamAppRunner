package com.komok.appchanger;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;

import com.komok.common.AbstractBaseAdapter;
import com.komok.common.AbstractEnumerator;
import com.komok.common.BaseHelper;
import com.komok.common.Tile;

public class AppListAdapter extends AbstractBaseAdapter<Tile> {

	private final PackageManager mPackageManager;

	@SuppressWarnings("unchecked")
	public AppListAdapter(Context context, BaseHelper.Apps apps) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPackageManager = context.getPackageManager();

		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		List<ApplicationInfo> list = mPackageManager.getInstalledApplications(PackageManager.GET_META_DATA);
		List<ApplicationInfo> userAppList = new ArrayList<ApplicationInfo>();
		List<ApplicationInfo> sysAppList = new ArrayList<ApplicationInfo>();

		for (ApplicationInfo app : list) {
			if (isUserApp(app)) {
				userAppList.add(app);
			} else {
				sysAppList.add(app);
			}
		}

		if (!BaseHelper.Apps.All.equals(apps)) {
			if (BaseHelper.Apps.Sys.equals(apps)) {
				list = sysAppList;
			} else if (BaseHelper.Apps.User.equals(apps)) {
				list = userAppList;
			}
		}

		mTiles = new ArrayList<Tile>();
		new ApplicationEnumerator(context, this).execute(list);
	}

	private boolean isUserApp(ApplicationInfo ai) {
		int mask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
		return (ai.flags & mask) == 0;
	}

	private class ApplicationEnumerator extends AbstractEnumerator<ApplicationInfo, Tile, AppListAdapter> {

		public ApplicationEnumerator(Context context, AbstractBaseAdapter<Tile> adapter) {
			super(context, adapter);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(List<ApplicationInfo>... params) {
			final PackageManager packageManager = mContext.getPackageManager();

			List<ApplicationInfo> list = params[0];

			Collections.sort(list, new Comparator<ApplicationInfo>() {
				final Collator mCollator;

				{
					mCollator = Collator.getInstance();
				}

				public int compare(ApplicationInfo info1, ApplicationInfo info2) {
					return mCollator.compare(info1.loadLabel(packageManager), info2.loadLabel(packageManager));
				}
			});

			for (ApplicationInfo info : list) {

				Drawable thumb = info.loadIcon((packageManager));
				// Intent launchIntent =
				// packageManager.getLaunchIntentForPackage(info.packageName);
				Intent launchIntent = BaseHelper.getIntent(info.packageName, packageManager);
				String label = (String) packageManager.getApplicationLabel(info);

				if (launchIntent != null && label != null) {
					Tile application = new Tile(thumb, launchIntent, label);
					publishProgress(application);
				}
			}
			// Send a null object to show loading is finished
			publishProgress((Tile) null);

			return null;
		}

	}
}
