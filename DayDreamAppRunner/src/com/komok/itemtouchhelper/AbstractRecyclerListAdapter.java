package com.komok.itemtouchhelper;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.komok.dreamapprunner.R;


public abstract class AbstractRecyclerListAdapter<T> extends RecyclerView.Adapter<AbstractRecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

	protected List<T> mTile;
    protected final PackageManager mPackageManager;
    protected final OnStartDragListener mDragStartListener;
    protected OnClickListener mViewOnClickListener;

    public AbstractRecyclerListAdapter(final Context context, OnStartDragListener dragStartListener, List<T> selectedTilesList) {
        mDragStartListener = dragStartListener;
        mTile = selectedTilesList;
        mPackageManager = context.getPackageManager();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tile, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onItemDismiss(int position) {
    	mTile.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mTile, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return mTile.size();
    }

    /**
     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public final TextView textView;
        public final ImageView imageView;
        public final CheckBox checkBox;
        public final View holderView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            holderView = itemView;
            textView = (TextView) itemView.findViewById(R.id.tile_label);
            imageView = (ImageView) itemView.findViewById(R.id.tile_image);
            checkBox = (CheckBox) itemView.findViewById(R.id.custom_checkbox);
            checkBox.setVisibility(View.GONE);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
