package com.kenbie.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.listeners.ProfileOptionListener;
import com.kenbie.model.OptionsData;
import com.kenbie.model.ProfileInfo;

import java.util.ArrayList;

/**
 * Created by rajaw on 7/28/2017.
 */

public class ProfileViewStepsAdapter extends RecyclerView.Adapter<ProfileViewStepsAdapter.ViewHolder> implements ProfileOptionListener {
    private ArrayList<OptionsData> mOptionsData;
    private ProfileOptionListener myListeners;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public ProfileViewStepsAdapter(Context context, ArrayList<OptionsData> optionsData, ProfileOptionListener mListeners) {
        this.mOptionsData = optionsData;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.myListeners = mListeners;
    }

    @Override
    public ProfileViewStepsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = mLayoutInflater.inflate(R.layout.profile_show_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

       /* holder.oImg.setBackgroundResource(mOptionsData.get(position).getImgId());

        holder.oEdit.setTypeface(KenbieApplication.S_NORMAL);
        holder.oEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myListeners.getAction(mOptionsData.get(holder.getAdapterPosition()));
            }
        });

        if (isEdit)
            holder.oEdit.setVisibility(View.VISIBLE);
        else
        holder.oEdit.setVisibility(View.INVISIBLE);*/

        holder.oTitle.setTypeface(KenbieApplication.S_NORMAL);
        holder.aboutText.setTypeface(KenbieApplication.S_NORMAL);
        holder.oTitle.setText(mOptionsData.get(position).getName());

        try { // {"About", "Connect with me", "Information", "Disciplines", "Categories", "Language"}
            /*if (mOptionsData.get(position).getId() == 1) { // Gallery
                holder.aboutText.setVisibility(View.GONE);
                holder.rvOptionView.setVisibility(View.VISIBLE);
                holder.rvOptionView.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                holder.rvOptionView.setLayoutManager(linearLayoutManager);
                ProfileGalleryAdapter galleryAdapter = new ProfileGalleryAdapter(mContext, mProfileInfo.getGalleryList(), this, 1);
                holder.rvOptionView.setAdapter(galleryAdapter);
            } else */

            if (mOptionsData.get(position).getId() == 1) { // About
                holder.aboutText.setText(mOptionsData.get(position).getOptionData());

//                    holder.aboutText.setText("About Data Will Display");
                holder.aboutText.setVisibility(View.VISIBLE);
                holder.rvOptionView.setVisibility(View.GONE);
            } else if (mOptionsData.get(position).getId() == 2) { // Connect with me
                holder.aboutText.setVisibility(View.GONE);
                holder.rvOptionView.setVisibility(View.VISIBLE);
//                holder.rvOptionView.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false);
                holder.rvOptionView.setLayoutManager(linearLayoutManager);
                ProfileGalleryAdapter galleryAdapter = new ProfileGalleryAdapter(mContext, mOptionsData.get(position).getOptionDataArrayList(), this, 2);
                holder.rvOptionView.setAdapter(galleryAdapter);
            } else if (mOptionsData.get(position).getId() == 3) { // Information
                holder.aboutText.setVisibility(View.GONE);
                holder.rvOptionView.setVisibility(View.VISIBLE);
//                holder.rvOptionView.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
                holder.rvOptionView.setLayoutManager(linearLayoutManager);
                ProfileTextViewAdapter viewAdapter = new ProfileTextViewAdapter(mContext, mOptionsData.get(position).getOptionDataArrayList(), this, 1);
                holder.rvOptionView.setAdapter(viewAdapter);
            } else if (mOptionsData.get(position).getId() == 4) { // Disciplines
                holder.aboutText.setVisibility(View.VISIBLE);
                holder.rvOptionView.setVisibility(View.GONE);
                holder.aboutText.setText(mOptionsData.get(position).getOptionData());
//                holder.rvOptionView.setLayoutManager(new GridLayoutManager(mContext, 3));
//                ProfileTextViewAdapter viewAdapter = new ProfileTextViewAdapter(mContext, mOptionsData.get(position).getOptionDataArrayList(), this, 2);
//                holder.rvOptionView.setAdapter(viewAdapter);
            } else if (mOptionsData.get(position).getId() == 5) { // Categories
                holder.aboutText.setVisibility(View.VISIBLE);
                holder.rvOptionView.setVisibility(View.GONE);
                holder.aboutText.setText(mOptionsData.get(position).getOptionData());

//                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
//                holder.rvOptionView.setLayoutManager(linearLayoutManager);

//                holder.rvOptionView.setHasFixedSize(true);
//                holder.rvOptionView.setLayoutManager(new GridLayoutManager(mContext, 3));
//                ProfileTextViewAdapter viewAdapter = new ProfileTextViewAdapter(mContext, mOptionsData.get(position).getOptionDataArrayList(), this, 2);
//                holder.rvOptionView.setAdapter(viewAdapter);
            } else if (mOptionsData.get(position).getId() == 6) { // Languages
                holder.aboutText.setVisibility(View.VISIBLE);
                holder.rvOptionView.setVisibility(View.GONE);
                holder.aboutText.setText(mOptionsData.get(position).getOptionData());

//                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
//                holder.rvOptionView.setLayoutManager(linearLayoutManager);

//                holder.rvOptionView.setHasFixedSize(true);
//                holder.rvOptionView.setLayoutManager(new GridLayoutManager(mContext, 3));
//                ProfileTextViewAdapter viewAdapter = new ProfileTextViewAdapter(mContext, mOptionsData.get(position).getOptionDataArrayList(), this, 2);
//                holder.rvOptionView.setAdapter(viewAdapter);
            } else {
                holder.aboutText.setVisibility(View.GONE);
                holder.aboutText.setVisibility(View.VISIBLE);
                holder.rvOptionView.setVisibility(View.GONE);
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
        myListeners.getDataList(value, position, type);
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
