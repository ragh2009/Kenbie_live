package com.kenbie.adapters;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.model.Option;

import java.util.ArrayList;

/**
 * Created by rajaw on 10/9/2017.
 */

public class CastingInfoAdapter extends RecyclerView.Adapter<CastingInfoAdapter.ViewHolder> {
    private ArrayList<Option> castingInfo;

    public CastingInfoAdapter(ArrayList<Option> info) {
        this.castingInfo = info;
    }

    @Override
    public CastingInfoAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.casting_user_info_item, viewGroup, false);
        return new CastingInfoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CastingInfoAdapter.ViewHolder holder, int position) {
        holder.cInfoImg.setImageResource(castingInfo.get(position).getId());
        holder.cInfoName.setText(castingInfo.get(position).getTitle());
        holder.cInfoDisc.setText(Html.fromHtml(castingInfo.get(position).getCode()));
    }


    @Override
    public int getItemCount() {
        return castingInfo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView cInfoImg;
        private TextView cInfoName, cInfoDisc;

        public ViewHolder(View view) {
            super(view);

            cInfoImg = (ImageView) view.findViewById(R.id.c_info_img);
            cInfoName = (TextView) view.findViewById(R.id.c_info_name);
            cInfoName.setTypeface(KenbieApplication.S_SEMI_BOLD);
            cInfoDisc = (TextView) view.findViewById(R.id.c_info_disc);
            cInfoDisc.setTypeface(KenbieApplication.S_NORMAL);
        }
    }
}
