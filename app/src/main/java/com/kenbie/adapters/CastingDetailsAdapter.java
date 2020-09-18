package com.kenbie.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.listeners.ProfileOptionListener;
import com.kenbie.model.OptionsData;
import com.kenbie.model.ProfileInfo;

import java.util.ArrayList;

/**
 * Created by rajaw on 11/14/2017.
 */

public class CastingDetailsAdapter extends RecyclerView.Adapter<com.kenbie.adapters.CastingDetailsAdapter.ViewHolder> implements ProfileOptionListener {
    private ArrayList<OptionsData> mOptionsData;
    private Context mContext;
    private boolean isEdit;
    private LayoutInflater mLayoutInflater;

    public CastingDetailsAdapter(Context context, ArrayList<OptionsData> optionsData, boolean type) {
        this.mOptionsData = optionsData;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.isEdit = type;
    }

    @Override
    public com.kenbie.adapters.CastingDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = mLayoutInflater.inflate(R.layout.casting_details_cell_view, parent, false);

        com.kenbie.adapters.CastingDetailsAdapter.ViewHolder vh = new com.kenbie.adapters.CastingDetailsAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final com.kenbie.adapters.CastingDetailsAdapter.ViewHolder holder, int position) {

        holder.oTitle.setTypeface(KenbieApplication.S_SEMI_BOLD);
        holder.oTitle.setText(mOptionsData.get(position).getName());
        holder.aboutText.setTypeface(KenbieApplication.S_NORMAL);

        try {
            if (mOptionsData.get(position).getId() == 1) { // Requirements
                holder.aboutText.setText(mOptionsData.get(position).getOptionData());
                holder.aboutText.setVisibility(View.VISIBLE);
                holder.rvOptionView.setVisibility(View.GONE);
            } else if (mOptionsData.get(position).getId() == 2) { // Preferences
                holder.aboutText.setVisibility(View.GONE);
                holder.rvOptionView.setVisibility(View.VISIBLE);
//                holder.rvOptionView.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext,RecyclerView.VERTICAL, false);
                holder.rvOptionView.setLayoutManager(linearLayoutManager);
                CastingItemAdapter viewAdapter = new CastingItemAdapter(mContext, mOptionsData.get(position).getOptionArrayList(), this, 1);
                holder.rvOptionView.setAdapter(viewAdapter);
            } else { // Categories
                holder.aboutText.setVisibility(View.GONE);
                holder.rvOptionView.setVisibility(View.VISIBLE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
                holder.rvOptionView.setLayoutManager(linearLayoutManager);
                CastingItemAdapter viewAdapter = new CastingItemAdapter(mContext, mOptionsData.get(position).getOptionArrayList(), this, 2);
                holder.rvOptionView.setAdapter(viewAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Bind data on view according type
//        setDataOnView(mContext, holder.rvOptionView, mOptionsData, mProfileInfo);
    }

    private void setDataOnView(Context context, LinearLayout rvOptionValue, ArrayList<OptionsData> mOptionsData, ProfileInfo mProfileInfo) {
        try {
            rvOptionValue.removeAllViews();
//            String[] name = {"Gallery", "About", "Connect with me", "Information", "Disciplines", "Categories", "Languages"};
            for (int i = 0; i < mOptionsData.size(); i++) {
                if (mOptionsData.get(i).getId() == 1) {
                    // Bind image array
                    rvOptionValue.addView(null);

                } else if (mOptionsData.get(i).getId() == 2) { // About
                    TextView aboutTxt = new TextView(context);
                    if (mProfileInfo.getAbout_user() != null && !mProfileInfo.getAbout_user().equalsIgnoreCase("null"))
                        aboutTxt.setText(mProfileInfo.getAbout_user());
                    else
                        aboutTxt.setText("About Data Will Display");

                    aboutTxt.setTextColor(context.getResources().getColor(R.color.gray_light));
                    aboutTxt.setTextSize(context.getResources().getDimension(R.dimen.text_size_10));
                    aboutTxt.setTypeface(KenbieApplication.S_NORMAL);
                    rvOptionValue.addView(aboutTxt);
                } else {
                    rvOptionValue.addView(null);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mOptionsData.size();
    }

    @Override
    public void getAction(OptionsData value) {

    }

    @Override
    public void getDataList(ArrayList<OptionsData> value, int position, int type) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView oTitle, aboutText;
        private RecyclerView rvOptionView;

        public ViewHolder(View mView) {
            super(mView);
            oTitle = (TextView) mView.findViewById(R.id.o_title);
            aboutText = (TextView) mView.findViewById(R.id.about_text);
            rvOptionView = (RecyclerView) mView.findViewById(R.id.rv_option_value);
        }
    }
}
