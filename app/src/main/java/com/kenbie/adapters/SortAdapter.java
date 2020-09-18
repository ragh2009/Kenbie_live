package com.kenbie.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.model.Option;

import java.util.ArrayList;

/**
 * Created by rajaw on 10/31/2017.
 */

public class SortAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Option> mData;
    private LayoutInflater mLayoutInflater;
    private int mType;

    public SortAdapter(Context context, ArrayList<Option> value, int type) {
        mData = value;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mType = type;
    }

    public int getCount() {
        return mData.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = mLayoutInflater.inflate(R.layout.sort_list_cell_view, null);
        else ;

        TextView mTitle = (TextView) convertView.findViewById(R.id.m_title);
        mTitle.setText(mData.get(position).getTitle());
        mTitle.setTypeface(KenbieApplication.S_SEMI_BOLD);

        ImageView imgDone = (ImageView) convertView.findViewById(R.id.img_done);
        if (mType == position)
            imgDone.setVisibility(View.VISIBLE);
        else
            imgDone.setVisibility(View.INVISIBLE);

        return convertView;
    }
}
