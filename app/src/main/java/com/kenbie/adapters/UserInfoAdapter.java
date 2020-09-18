package com.kenbie.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSpinner;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.jaredrummler.materialspinner.MaterialSpinnerAdapter;
import com.jaredrummler.materialspinner.MaterialSpinnerBaseAdapter;
import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.listeners.InfoListener;
import com.kenbie.model.Option;
import com.kenbie.model.OptionsData;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by rajaw on 9/8/2017.
 */

public class UserInfoAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<OptionsData> mData;
    private LayoutInflater mLayoutInflater;
    private InfoListener mListeners;

    public UserInfoAdapter(Context context, ArrayList<OptionsData> value, InfoListener listener) {
        mData = value;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mListeners = listener;
    }


    public void refreshData(ArrayList<OptionsData> mSocialList) {
//        mData = mSocialList;
//        notifyDataSetChanged();
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
        View view = convertView;
        final ViewHolder viewHolder;

        if (convertView == null) {
            view = mLayoutInflater.inflate(R.layout.info_cell_view, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) view.findViewById(R.id.t_title);
            viewHolder.title.setTypeface(KenbieApplication.S_NORMAL);
            viewHolder.compatSpinner = (AppCompatSpinner) view.findViewById(R.id.t_spinner);
//            viewHolder.compatSpinner = (MaterialSpinner) view.findViewById(R.id.t_spinner);
            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();

        final OptionsData data = mData.get(position);

        viewHolder.title.setText(data.getName());
        viewHolder.compatSpinner.setPrompt(data.getName());
//        viewHolder.compatSpinner.setDropDownVerticalOffset(mContext.getResources().getDimensionPixelOffset(R.dimen.size_10));
        InfoSingleChoiceAdapter adapter = new InfoSingleChoiceAdapter(mLayoutInflater, data.getOptionArrayList(), mData.get(position).getImgId());
        viewHolder.compatSpinner.setAdapter(adapter);

/*        viewHolder.compatSpinner.setItems(data.getData());
        viewHolder.compatSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position1, long id, Object item) {
                mListeners.getInfoValue(position, position1);
                mData.get(position).setImgId(position1);
            }
        });

        viewHolder.compatSpinner.setSelectedIndex( mData.get(position).getImgId());*/

       viewHolder.compatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position1, long id) {
                mListeners.getInfoValue(position, position1);
                mData.get(position).setImgId(position1);
                adapter.updateSelection(position1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        viewHolder.compatSpinner.setSelection(mData.get(position).getImgId());

//        mData.get(position).setImgId(viewHolder.compatSpinner.getSelectedItemPosition());
//        mListeners.getInfoValue(null, viewHolder.compatSpinner.getSelectedItemPosition());


//            viewHolder.title.setText(mData.get(position).getName());
//            viewHolder.compatSpinner.setSelection(mData.get(position).getImgId());

//        ViewHolder holder = (ViewHolder) view.getTag();

        return view;
    }

    public class ViewHolder {
        private TextView title;
        private AppCompatSpinner compatSpinner;
//        private MaterialSpinner compatSpinner;
    }
}

