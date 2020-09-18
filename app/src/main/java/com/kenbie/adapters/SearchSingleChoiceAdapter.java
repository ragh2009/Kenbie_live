package com.kenbie.adapters;

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
import com.kenbie.model.OptionsData;

import java.util.ArrayList;

/**
 * Created by raghu on 2/25/2020.
 */

public class SearchSingleChoiceAdapter extends BaseAdapter {
    private ArrayList<OptionsData> mData;
    private LayoutInflater mLayoutInflater;
    private int selPos = -1;

    public SearchSingleChoiceAdapter(LayoutInflater mLayoutInflater, ArrayList<OptionsData> optionArrayList, int selPos) {
        this.mLayoutInflater = mLayoutInflater;
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
        try {
            TextView sTitle = (TextView) convertView.findViewById(R.id.s_title);
            sTitle.setText(mData.get(position).getName());
            sTitle.setTypeface(KenbieApplication.S_NORMAL);
            if (position == 0)
                sTitle.setTextColor(Color.parseColor("#b3b3b3"));
            else
                sTitle.setTextColor(Color.parseColor("#7F7F7F"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    public View getDropDownView(final int position, View convertView,
                                final ViewGroup parent) {
        if (convertView == null) {
//            convertView = mLayoutInflater.inflate(android.R.layout.select_dialog_singlechoice, null);
            convertView = mLayoutInflater.inflate(R.layout.info_spinner_drop_down_view, null);
        }
//        TextView text = (TextView) convertView.findViewById(R.id.s_title);
        ((TextView) convertView.findViewById(R.id.s_title)).setVisibility(View.GONE);
        CheckedTextView text = (CheckedTextView) convertView.findViewById(R.id.s_title1);
        text.setVisibility(View.VISIBLE);
        text.setText(mData.get(position).getName());
        text.setTypeface(KenbieApplication.S_NORMAL);
        text.setTextColor(Color.parseColor("#7F7F7F"));
        if (selPos == position)
            text.setChecked(true);
        else
            text.setChecked(false);

//        if(position == 0)
//            text.setTextColor(Color.parseColor("#b3b3b3"));
//        else
//            text.setTextColor(Color.parseColor("#7F7F7F"));
       /* text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.getInfoValue(null, position);
            }
        });*/
        return convertView;
    }

}