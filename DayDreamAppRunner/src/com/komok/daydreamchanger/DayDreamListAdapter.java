package com.komok.daydreamchanger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.service.dreams.DreamService;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;

import com.komok.common.AbstractBaseAdapter;
import com.komok.common.AbstractEnumerator;
import com.komok.common.BaseHelper;
import com.komok.common.Tile;

public class DayDreamListAdapter extends AbstractBaseAdapter<Tile> {

	private final PackageManager mPackageManager;
	private static final String LOG_TAG = "DayDreamListAdapter";
	private Class clasz;
	private Field fieldDream;
	private Field fieldDreamActivity;
	private int[] attributes;
	private int settingsActivityId;
	private final String appLabel;

	@SuppressWarnings("unchecked")
	public DayDreamListAdapter(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPackageManager = context.getPackageManager();
		appLabel = BaseHelper.getApplicationName(context);
		List<ResolveInfo> list = mPackageManager.queryIntentServices(new Intent(DreamService.SERVICE_INTERFACE), PackageManager.GET_META_DATA);
		mTiles = new ArrayList<Tile>();
		new DayDreamEnumerator(context, this).execute(list);
	}

	private class DayDreamEnumerator extends AbstractEnumerator<ResolveInfo, Tile, DayDreamListAdapter> {

		public DayDreamEnumerator(Context context, AbstractBaseAdapter<Tile> adapter) {
			super(context, adapter);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(List<ResolveInfo>... params) {
			final PackageManager packageManager = mContext.getPackageManager();

			List<ResolveInfo> list = params[0];

			try {
				clasz = Class.forName("com.android.internal.R$styleable");
				fieldDream = clasz.getDeclaredField("Dream");
				fieldDreamActivity = clasz.getDeclaredField("Dream_settingsActivity");

				fieldDream.setAccessible(true);
				attributes = (int[]) fieldDream.get(null);

				fieldDreamActivity.setAccessible(true);
				settingsActivityId = (Integer) fieldDreamActivity.get(null);

			} catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
				Log.w(LOG_TAG, "Error ", e);
			}

			Collections.sort(list, new Comparator<ResolveInfo>() {
				final Collator mCollator;

				{
					mCollator = Collator.getInstance();
				}

				public int compare(ResolveInfo info1, ResolveInfo info2) {
					return mCollator.compare(info1.loadLabel(packageManager), info2.loadLabel(packageManager));
				}
			});

			for (ResolveInfo resolveInfo : list) {

				Drawable thumb = resolveInfo.serviceInfo.loadIcon(packageManager);
				Intent launchIntent = new Intent(DreamService.SERVICE_INTERFACE);
				launchIntent.setClassName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);
				launchIntent.setPackage(resolveInfo.serviceInfo.packageName);
				Tile tile = new Tile(thumb, launchIntent, (String) resolveInfo.serviceInfo.loadLabel(mPackageManager));

				ComponentName componentName = getSettingsComponentName(mPackageManager, resolveInfo);
				tile.mSettingsActivity = componentName == null ? null : componentName.getClassName();
				if(!tile.mLabel.equals(appLabel))
					publishProgress(tile);
			}
			// Send a null object to show loading is finished
			publishProgress((Tile) null);

			return null;
		}

		private ComponentName getSettingsComponentName(PackageManager pm, ResolveInfo resolveInfo) {
			if (resolveInfo == null || resolveInfo.serviceInfo == null || resolveInfo.serviceInfo.metaData == null)
				return null;
			String cn = null;
			XmlResourceParser parser = null;
			Exception caughtException = null;
			try {
				parser = resolveInfo.serviceInfo.loadXmlMetaData(pm, DreamService.DREAM_META_DATA);
				if (parser == null) {
					Log.w(LOG_TAG, "No " + DreamService.DREAM_META_DATA + " meta-data");
					return null;
				}
				Resources res = pm.getResourcesForApplication(resolveInfo.serviceInfo.applicationInfo);
				AttributeSet attrs = Xml.asAttributeSet(parser);
				int type;
				while ((type = parser.next()) != XmlPullParser.END_DOCUMENT && type != XmlPullParser.START_TAG) {
				}
				String nodeName = parser.getName();
				if (!"dream".equals(nodeName)) {
					Log.w(LOG_TAG, "Meta-data does not start with dream tag");
					return null;
				}
				TypedArray sa = res.obtainAttributes(attrs, attributes);
				cn = sa.getString(settingsActivityId);
				sa.recycle();
			} catch (NameNotFoundException e) {
				caughtException = e;
			} catch (IOException e) {
				caughtException = e;
			} catch (XmlPullParserException e) {
				caughtException = e;
			} finally {
				if (parser != null)
					parser.close();
			}
			if (caughtException != null) {
				Log.w(LOG_TAG, "Error parsing : " + resolveInfo.serviceInfo.packageName, caughtException);
				return null;
			}
			if (cn != null && cn.indexOf('/') < 0) {
				cn = resolveInfo.serviceInfo.packageName + "/" + cn;
			}
			return cn == null ? null : ComponentName.unflattenFromString(cn);
		}

	}
}
