package com.kenbie.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kenbie.R;
import com.kenbie.listeners.NavigationDrawerCallbacks;
import com.kenbie.model.LeftItem;

import java.util.ArrayList;

public class LeftPanelAdapter extends RecyclerView.Adapter<LeftPanelAdapter.ViewHolder> {
    private ArrayList<LeftItem> mDataSet;
    private NavigationDrawerCallbacks mListeners;

    // Provide a suitable constructor (depends on the kind of dataset)
    public LeftPanelAdapter(ArrayList<LeftItem> myDataSet1, NavigationDrawerCallbacks navigationDrawerFragment) {
        mDataSet = myDataSet1;
        mListeners = navigationDrawerFragment;
    }

    public void refreshData(ArrayList<LeftItem> myDataSet1){
        mDataSet = myDataSet1;
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LeftPanelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.left_nav_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTitleTxt.setText(mDataSet.get(position).getTitle());
        holder.iconImg.setBackgroundResource(mDataSet.get(position).getImage());
        holder.navAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListeners.onNavigationDrawerItemSelected(mDataSet.get(holder.getLayoutPosition()));
            }
        });

        if(mDataSet.get(position).getCount() != 0) {
            holder.nCountTxt.setVisibility(View.VISIBLE);
            holder.nCountTxt.setText(mDataSet.get(position).getCount()+"");
        }else
            holder.nCountTxt.setVisibility(View.GONE);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTitleTxt, nCountTxt;
        public ImageView iconImg;
        public LinearLayout navAction;

        public ViewHolder(View v) {
            super(v);
            mTitleTxt = (TextView) v.findViewById(R.id.n_title);
            nCountTxt = (TextView) v.findViewById(R.id.n_count_txt);
            iconImg = (ImageView) v.findViewById(R.id.n_img);
            navAction = (LinearLayout) v.findViewById(R.id.nav_action);
        }
    }
}