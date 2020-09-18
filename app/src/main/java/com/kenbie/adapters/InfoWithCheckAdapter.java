package com.kenbie.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.listeners.UserActionListener;
import com.kenbie.model.UserTypeData;

import java.util.ArrayList;

/**
 * Created by rajaw on 02/06/2019.
 */

public class InfoWithCheckAdapter extends RecyclerView.Adapter<InfoWithCheckAdapter.ViewHolder> {
    private String[] info;
    private UserActionListener myListeners;
    private LayoutInflater mLayoutInflater;

    public InfoWithCheckAdapter(Context context, String[] data) {
        this.info = data;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public InfoWithCheckAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = mLayoutInflater.inflate(R.layout.check_info_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(info[position]);
    }


    @Override
    public int getItemCount() {
        return info.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView title;

        public ViewHolder(View mView) {
            super(mView);
            title = (TextView) mView.findViewById(R.id.title);
            title.setTypeface(KenbieApplication.S_NORMAL);
        }
    }
}
