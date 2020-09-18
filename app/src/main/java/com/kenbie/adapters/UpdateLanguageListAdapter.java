package com.kenbie.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SwitchCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.listeners.InfoListener;
import com.kenbie.listeners.LanguageListener;
import com.kenbie.listeners.MsgUserActionListeners;
import com.kenbie.model.MsgUserItem;
import com.kenbie.model.OptionsData;
import com.kenbie.util.Constants;

import java.util.ArrayList;

/**
 * Created by rajaw on 8/23/2019.
 */

public class UpdateLanguageListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<OptionsData> mUserList;
    private ArrayList<OptionsData> oUserList;
    private LayoutInflater mLayoutInflater;
    private LanguageListener mListeners;
    private ItemFilter mFilter = new ItemFilter();


    public UpdateLanguageListAdapter(Context context, ArrayList<OptionsData> userList, LanguageListener listener) {
        mContext = context;
        mUserList = userList;
        oUserList = userList;
        mLayoutInflater = LayoutInflater.from(mContext);
        mListeners = listener;
        if (mUserList == null)
            mUserList = new ArrayList<>();
        if (oUserList == null)
            oUserList = new ArrayList<>();
    }


    public void refreshData(ArrayList<OptionsData> userList) {
        mUserList = userList;
        oUserList = userList;
        if (mUserList == null)
            mUserList = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void refreshSearchData(ArrayList<OptionsData> userList) {
        mUserList = userList;
        if (mUserList == null)
            mUserList = new ArrayList<>();
        notifyDataSetChanged();
    }

    public int getCount() {
        return mUserList.size();
    }

    public Object getItem(int position) {
        return mUserList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = mLayoutInflater.inflate(R.layout.text_cell_view, null);
        else ;

        TextView title = (TextView) convertView.findViewById(R.id.title);
        title.setTypeface(KenbieApplication.S_NORMAL);
        title.setText(mUserList.get(position).getName());
        SwitchCompat switchCompat = (SwitchCompat) convertView.findViewById(R.id.m_switch);
        switchCompat.setVisibility(View.VISIBLE);
        ImageView arrowImg = ((ImageView) convertView.findViewById(R.id.arrow_img));
        arrowImg.setVisibility(View.GONE);

        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListeners.getSelectedMessageInfo(mUserList.get(position));
            }
        });

        if (mUserList.get(position).isActive())
            switchCompat.setChecked(true);
        else
            switchCompat.setChecked(false);

        return convertView;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<OptionsData> list = oUserList;
            int count = list.size();
            final ArrayList<OptionsData> sList = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                if (list.get(i).getName().toLowerCase().contains(filterString)) {
                    sList.add(list.get(i));
                }
            }

            results.values = sList;
            results.count = sList.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mUserList = (ArrayList<OptionsData>) results.values;
            notifyDataSetChanged();
        }
    }
}



