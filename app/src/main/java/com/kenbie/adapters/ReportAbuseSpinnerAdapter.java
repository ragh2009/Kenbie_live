package com.kenbie.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.model.Option;

import java.util.ArrayList;

/**
 * Created by rajaw on 9/8/2017.
 */

public class ReportAbuseSpinnerAdapter extends BaseAdapter {
    private ArrayList<Option> mData;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private int selPos = -1;

    public ReportAbuseSpinnerAdapter(Context context, ArrayList<Option> optionArrayList, int selPos) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.mData = optionArrayList;
        if (mData == null)
            mData = new ArrayList<>();
        this.selPos = selPos;
    }

    public void updateSelection(int position1) {
        this.selPos = position1;
    }

    public int getCount() {
        return mData.size();
    }

    public Object getItem(int position) {
        return mData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = mLayoutInflater.inflate(R.layout.info_spinner_cell_view, null);
        else ;

        TextView sTitle = (TextView) convertView.findViewById(R.id.s_title);
        sTitle.setText(mData.get(position).getTitle());
        sTitle.setTypeface(KenbieApplication.S_NORMAL);
        if (position == 0)
            sTitle.setTextColor(mContext.getResources().getColor(R.color.c_cccccc));
        else
            sTitle.setTextColor(mContext.getResources().getColor(R.color.gray_light));
        return convertView;
    }

    public View getDropDownView(final int position, View convertView,
                                final ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.info_spinner_drop_down_view, null);
        }
        ((TextView) convertView.findViewById(R.id.s_title)).setVisibility(View.GONE);
        CheckedTextView text = (CheckedTextView) convertView.findViewById(R.id.s_title1);
        text.setVisibility(View.VISIBLE);
        text.setText(mData.get(position).getTitle());
        text.setTypeface(KenbieApplication.S_NORMAL);
        text.setTextColor(Color.parseColor("#7F7F7F"));
        if (selPos == position)
            text.setChecked(true);
        else
            text.setChecked(false);

       /* text.setText(mData.get(position).getTitle());
        text.setTypeface(KenbieApplication.S_NORMAL);
        if (position == 0)
            text.setTextColor(mContext.getResources().getColor(R.color.c_cccccc));
        else
            text.setTextColor(mContext.getResources().getColor(R.color.gray_light));*/

       /* text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.getInfoValue(null, position);
            }
        });*/
        return convertView;
    }
}