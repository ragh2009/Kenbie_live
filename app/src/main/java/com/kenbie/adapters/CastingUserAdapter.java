package com.kenbie.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.listeners.CastingUserListeners;
import com.kenbie.model.CastingUser;
import com.kenbie.util.Constants;
import com.kenbie.util.Utility;

import java.util.ArrayList;

/**
 * Created by rajaw on 9/21/2017.
 */

public class CastingUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_FOOTER = 1;
    public ArrayList<CastingUser> castingUsers;
    private Context mContext;
    private CastingUserListeners mListeners;
    private Utility utility;
    private int castingType = 0;
    private RequestOptions options = null;
    private SharedPreferences mPref;
    private boolean isLastPage;
    private LayoutInflater mLayoutInflater;
    private String bTitle = "";

    public CastingUserAdapter(Context context, ArrayList<CastingUser> castingUsers, CastingUserListeners castingUserListeners, int type, SharedPreferences mPref, boolean lPage) {
        this.castingUsers = castingUsers;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.mListeners = castingUserListeners;
        utility = new Utility();
        this.castingType = type;
        this.isLastPage = lPage;
        options = new RequestOptions()
                .circleCrop()
                .placeholder(mContext.getResources().getDrawable(R.drawable.img_c_user_dummy))
                .priority(Priority.HIGH);
        this.mPref = mPref;
        bTitle = mPref.getString("56", "You've reached the end of the list");
    }

    public void refreshData(ArrayList<CastingUser> castingUsers, boolean lPage, int cType) {
        this.castingUsers = castingUsers;
        this.isLastPage = lPage;
        castingType = cType;
        if (castingUsers == null)
            castingUsers = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TYPE_FOOTER) {
            //Inflating footer view
            itemView = mLayoutInflater.inflate(R.layout.footer_view, parent, false);
            return new FooterViewHolder(itemView);
        } else {
            itemView = mLayoutInflater.inflate(R.layout.casting_user_cell_view, parent, false);
            return new ItemViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder1, final int position) {
        if (holder1 instanceof FooterViewHolder) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder1;
            footerHolder.footerText.setText(bTitle);
        } else if (holder1 instanceof ItemViewHolder) {
            ItemViewHolder holder = (ItemViewHolder) holder1;
            holder.cName.setText(castingUsers.get(position).getCasting_title());

            String gender = getGenderFromData(castingUsers.get(position).getCasting_gender());
            if (gender != null && !gender.equalsIgnoreCase("null") && gender.length() > 0) {
                holder.cGender.setText(getGenderFromData(castingUsers.get(position).getCasting_gender()));
                holder.cGender.setVisibility(View.VISIBLE);
            } else
                holder.cGender.setVisibility(View.GONE);

            holder.cUserLoc.setText(castingUsers.get(position).getCasting_location());

            Glide.with(mContext).load(castingUsers.get(position).getCasting_img()).apply(options).into(holder.cUserImg);

//        String dateStr = "Casting Dates: <font color=\"#b3b3b3\">" + utility.getDisplayDateFormat(castingUsers.get(position).getCasting_start_date()) + "</font> to <font color=\"#b3b3b3\">" + utility.getDisplayDateFormat(castingUsers.get(position).getCasting_end_date()) + "</font>";
            holder.cDates.setText(castingUsers.get(position).getCasting_start_date() + " " + mPref.getString("323", "to") + " " + castingUsers.get(position).getCasting_end_date());

//        holder.cReq.setText(Html.fromHtml("Requirements: <font color=\"#b3b3b3\">" + castingUsers.get(position).getCasting_requirement() + "</font>"));
//        holder.cPayment.setText(Html.fromHtml("Payment: <font color=\"#b3b3b3\">" + castingUsers.get(position).getCasting_fee() + "</font>"));
//        holder.cAddress.setText(Html.fromHtml("Address: <font color=\"#b3b3b3\">" + castingUsers.get(position).getCasting_address() + "</font>"));

//        if (type == 2) {
//            holder.cReq.setVisibility(View.GONE);
//            holder.cPayment.setVisibility(View.GONE);
//            holder.cAddress.setVisibility(View.GONE);
//            holder.cUserImg.setBackgroundResource(dummyImgs[position]);
//        } else {
//            Glide.with(mContext).load(Constants.BASE_IMAGE_URL + castingUsers.get(position).getUser_pic()).apply(RequestOptions.circleCropTransform()).into(holder.cUserImg);
//            holder.cReq.setVisibility(View.VISIBLE);
//            holder.cPayment.setVisibility(View.VISIBLE);
//            holder.cAddress.setVisibility(View.VISIBLE);
//        }


            holder.userAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListeners.getUserDetails(position, castingType);
                }
            });

            holder.editCasting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListeners.getUserDetails(position, 6);
                }
            });

            holder.deleteCasting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListeners.getUserDetails(position, 7);
                }
            });

            holder.castingPayNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListeners.getUserDetails(position, 5);
                }
            });

            holder.viewInstModel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListeners.getUserDetails(position, 4);
                }
            });

            if (castingType == 3) {
//            Glide.with(mContext).load(Constants.BASE_MY_CASTING_IMAGE_URL + castingUsers.get(position).getCasting_img()).apply(RequestOptions.circleCropTransform()).into(holder.cUserImg);
//            Glide.with(mContext).load(Constants.BASE_MY_CASTING_IMAGE_URL + castingUsers.get(position).getCasting_img()).apply(options).into(holder.cUserImg);
                holder.castingAction.setVisibility(View.VISIBLE);
                if (castingUsers.get(position).getIs_paid() == 0) {
                    holder.castingPayNow.setVisibility(View.VISIBLE);
                    holder.viewInstModel.setVisibility(View.GONE);
                } else {
                    holder.castingPayNow.setVisibility(View.GONE);
                    holder.viewInstModel.setVisibility(View.VISIBLE);
                }
            } else
                holder.castingAction.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        if (castingUsers == null) {
            return 0;
        }
        if (isLastPage)
            return castingUsers.size() + 1;

        return castingUsers.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isLastPage && position == castingUsers.size()) {
            return TYPE_FOOTER;
        }

        return super.getItemViewType(position);
    }

    public String getGenderFromData(String casting_gender) {
        String value = "";
        try {
            int gender = Integer.valueOf(casting_gender);
            if (gender == 1)
                value = mPref.getString("31", "Male");
            else if (gender == 2)
                value = mPref.getString("32", "Female");
            else
                value = mPref.getString("72", "Both");

            //                    value.setCasting_gender(id == 1 ? mActivity.mPref.getString("31", "Male") : mActivity.mPref.getString("32", "Female"));

        } catch (Exception e) {
//            e.printStackTrace();
        }

        return value;
    }

    public void addItem(CastingUser castingUser) {
        castingUsers.add(castingUser);
        notifyItemInserted(castingUsers.size());
    }

    public void removeItem(int position) {
        castingUsers.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, castingUsers.size());
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView cUserImg;
        private TextView cName, cGender, cUserLoc, cDatesTitle, cDates, viewInstModel, castingPayNow;
        private LinearLayout userAction, castingAction;
        private TextView editCasting, deleteCasting;

        public ItemViewHolder(View view) {
            super(view);

            userAction = (LinearLayout) view.findViewById(R.id.user_action);
            castingAction = (LinearLayout) view.findViewById(R.id.casting_action);
            cUserImg = (ImageView) view.findViewById(R.id.c_user_img);
            cName = (TextView) view.findViewById(R.id.c_name);
            cName.setTypeface(KenbieApplication.S_NORMAL);
            cGender = (TextView) view.findViewById(R.id.c_gender);
            cGender.setTypeface(KenbieApplication.S_NORMAL);
            cUserLoc = (TextView) view.findViewById(R.id.c_user_loc);
            cUserLoc.setTypeface(KenbieApplication.S_SEMI_BOLD);
            cDatesTitle = (TextView) view.findViewById(R.id.c_dates_title);
            cDatesTitle.setTypeface(KenbieApplication.S_SEMI_BOLD);
            cDatesTitle.setText(mPref.getString("338", "Date"));
            cDates = (TextView) view.findViewById(R.id.c_dates);
            cDates.setTypeface(KenbieApplication.S_NORMAL);
//            cReq = (TextView) view.findViewById(R.id.c_req);
//            cReq.setTypeface(KenbieApplication.S_NORMAL);
//            cPayment = (TextView) view.findViewById(R.id.c_payment);
//            cPayment.setTypeface(KenbieApplication.S_NORMAL);
//            cAddress = (TextView) view.findViewById(R.id.c_address);
//            cAddress.setTypeface(KenbieApplication.S_NORMAL);
            viewInstModel = (TextView) view.findViewById(R.id.view_inst_model);
            viewInstModel.setTypeface(KenbieApplication.S_NORMAL);
            viewInstModel.setText(mPref.getString("160", "View Interested Models"));

            castingPayNow = (TextView) view.findViewById(R.id.casting_pay_now);
            castingPayNow.setTypeface(KenbieApplication.S_NORMAL);
            castingPayNow.setText(mPref.getString("159", "Buy Now"));

            editCasting = (TextView) view.findViewById(R.id.btn_edit_casting);
            editCasting.setTypeface(KenbieApplication.S_NORMAL);
            editCasting.setText(mPref.getString("344", "Edit"));

            deleteCasting = (TextView) view.findViewById(R.id.btn_delete_casting);
            deleteCasting.setTypeface(KenbieApplication.S_NORMAL);
            deleteCasting.setText(mPref.getString("343", "Delete"));
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView footerText;

        public FooterViewHolder(View view) {
            super(view);
            footerText = (TextView) view.findViewById(R.id.footer_title);
            footerText.setText(bTitle);
            footerText.setTypeface(KenbieApplication.S_SEMI_BOLD);
        }
    }
}
