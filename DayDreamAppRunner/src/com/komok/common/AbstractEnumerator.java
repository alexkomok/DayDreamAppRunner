package com.komok.common;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.BaseAdapter;

import com.komok.wallpaperchanger.R;

public abstract class AbstractEnumerator<T, S extends Tile, A extends BaseAdapter> extends AsyncTask<List<T>, S, Void> {
	protected Context mContext;
	protected int mPosition;
	private ProgressDialog dialog;
	private AbstractBaseAdapter<S> mAdapter;

	public AbstractEnumerator(Context context, AbstractBaseAdapter<S> adapter) {
		super();
		mContext = context;
		mPosition = 0;
		dialog = new ProgressDialog(mContext);
		mAdapter = adapter;
	}

	@Override
	protected void onPreExecute() {
		this.dialog.setMessage(mContext.getString(R.string.wait));
		this.dialog.show();
	}

	@Override
	protected void onPostExecute(Void result) {
		if (mContext instanceof IItemChecked) {
			((IItemChecked) mContext).setItemChecked();
		}

		if (dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onProgressUpdate(S... infos) {
		for (S info : infos) {
			if (info == null) {
				mAdapter.notifyDataSetChanged();
				break;
			}
			if (info.mThumbnail != null) {
				info.mThumbnail.setDither(true);
			}
			if (mPosition < mAdapter.mTiles.size()) {
				mAdapter.mTiles.set(mPosition, info);
			} else {
				mAdapter.mTiles.add(info);
			}
			mPosition++;
		}
	}

}
