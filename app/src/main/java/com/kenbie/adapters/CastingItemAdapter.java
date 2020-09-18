package com.kenbie.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.listeners.ProfileOptionListener;
import com.kenbie.model.Option;

import java.util.ArrayList;

/**
 * Created by rajaw on 11/15/2017.
 */

public class CastingItemAdapter extends RecyclerView.Adapter<CastingItemAdapter.ViewHolder> {
    private ArrayList<Option> mOptionsData;
    private ProfileOptionListener myListeners;
    private Context mContext;
    private int mType;
    private LayoutInflater mLayoutInflater;

    public CastingItemAdapter(Context context, ArrayList<Option> optionsData, ProfileOptionListener mListeners, int type) {
        this.mOptionsData = optionsData;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.myListeners = mListeners;
        this.mType = type;
        if (mOptionsData == null)
            mOptionsData = new ArrayList<>();
    }

    @Override
    public CastingItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = mLayoutInflater.inflate(R.layout.info_item, parent, false);

        CastingItemAdapter.ViewHolder vh = new CastingItemAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final CastingItemAdapter.ViewHolder holder, int position) {
        holder.oTitle.setTypeface(KenbieApplication.S_NORMAL);
        holder.oTitle.setText(mOptionsData.get(position).getTitle());

        holder.oValue.setTypeface(KenbieApplication.S_NORMAL);

        if (mType == 1) {
            holder.oValue.setVisibility(View.VISIBLE);
            if (mOptionsData.get(position).getCode() != null && !mOptionsData.get(position).getCode().equalsIgnoreCase("null"))
                holder.oValue.setText(mOptionsData.get(position).getCode());
            else
                holder.oValue.setText("");
        } else {
            holder.oValue.setText("");
            holder.oValue.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mOptionsData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView oTitle, oValue;

        public ViewHolder(View mView) {
            super(mView);
            oTitle = (TextView) mView.findViewById(R.id.o_title);
            oValue = (TextView) mView.findViewById(R.id.o_value);
        }
    }
}
