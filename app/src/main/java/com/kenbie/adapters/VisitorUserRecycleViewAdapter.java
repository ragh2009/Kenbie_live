package com.kenbie.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.listeners.MsgUserActionListeners;
import com.kenbie.model.MsgUserItem;
import com.kenbie.model.UserItem;
import com.kenbie.util.Constants;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by rajaw on 7/30/2017.
 */

public class VisitorUserRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_FOOTER = 1;
    private RequestOptions options;
    private Context mContext;
    private ArrayList<UserItem> mUserList;
    private LayoutInflater mLayoutInflater;
    private MsgUserActionListeners msgUserActionListeners;
    private int mYear, hasMemberShip;
    private boolean isLastPage;
    private String bTitle = "";

    public VisitorUserRecycleViewAdapter(Context context, ArrayList<UserItem> userList, MsgUserActionListeners msgUserActionListeners, boolean lPage) {
        SharedPreferences mPref = context.getSharedPreferences("kPrefs", MODE_PRIVATE);
        hasMemberShip = mPref.getInt("MemberShip", 0);
        bTitle = mPref.getString("56", "You've reached the end of the list");
        mContext = context;
        mUserList = userList;
        this.isLastPage = lPage;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.msgUserActionListeners = msgUserActionListeners;
        Calendar mCalendar = Calendar.getInstance();
        mYear = mCalendar.get(Calendar.YEAR);
        options = new RequestOptions()
                .circleCrop()
                .placeholder(mContext.getResources().getDrawable(R.drawable.img_c_user_dummy))
                .priority(Priority.HIGH);
    }

    public void refreshData(ArrayList<UserItem> userList, boolean lPage) {
        mUserList = userList;
        if (mUserList == null)
            mUserList = new ArrayList<>();
        this.isLastPage = lPage;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TYPE_FOOTER) {
            //Inflating footer view
            itemView = mLayoutInflater.inflate(R.layout.footer_view, parent, false);
            return new FooterViewHolder(itemView);
        }

        itemView = mLayoutInflater.inflate(R.layout.visitor_user_cell_item, parent, false);
        return new ItemViewHolder(itemView);

/*        // create a new view
        View v = mLayoutInflater.inflate(R.layout.visitor_user_cell_item, parent, false);

        return new RecyclerView.ViewHolder(v);*/
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
        if (holder1 instanceof FooterViewHolder) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder1;
            footerHolder.footerText.setText(bTitle);
        } else if (holder1 instanceof ItemViewHolder) {
            ItemViewHolder holder = (ItemViewHolder) holder1;

            holder.userAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    msgUserActionListeners.updateFavStatus(3, holder.getAdapterPosition());
                }
            });

            if (mUserList.get(position).getBirthMonth() == 0)
                holder.bgImg.setBackgroundResource(R.drawable.bg_round_red);
            else
                holder.bgImg.setBackgroundResource(0);

            if (mUserList.get(position).getIsActive() == 1)
                holder.uStatus.setBackgroundResource(R.drawable.bg_round_green);
            else
                holder.uStatus.setBackgroundResource(R.drawable.bg_round_gray);

            if (mUserList.get(position).getBirthMonth() == 1)
                holder.uMsgImg.setBackgroundResource(R.drawable.ic_v_message_unread);
            else
                holder.uMsgImg.setBackgroundResource(R.drawable.ic_v_message_read);

            if (mUserList.get(position).getType() == 1)
                holder.uLikeImg.setBackgroundResource(R.drawable.ic_v_like_selected);
            else
                holder.uLikeImg.setBackgroundResource(R.drawable.ic_v_like_pv);

            if (mUserList.get(position).getBirthDay() == 1)
                holder.uFavImg.setBackgroundResource(R.drawable.ic_v_favourite_selected);
            else
                holder.uFavImg.setBackgroundResource(R.drawable.ic_v_favourite_pv);

            holder.uMsgImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MsgUserItem msgUserItem = new MsgUserItem();
                    msgUserItem.setUid(mUserList.get(holder.getAdapterPosition()).getId());
                    msgUserItem.setUser_name(mUserList.get(holder.getAdapterPosition()).getFirstName());
                    msgUserItem.setUser_img(mUserList.get(holder.getAdapterPosition()).getUserPic());
                    msgUserActionListeners.userConStart(msgUserItem);
                }
            });

            holder.uLikeImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                mUserList.get(position).setType(mUserList.get(position).getType() == 1 ? 0 : 1);
                    msgUserActionListeners.updateFavStatus(1, holder.getAdapterPosition());
//                notifyDataSetChanged();
                }
            });

            holder.uFavImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                mUserList.get(position).setBirthDay(mUserList.get(position).getBirthDay() == 1 ? 0 : 1);
                    msgUserActionListeners.updateFavStatus(2, holder.getAdapterPosition());
//                notifyDataSetChanged();
                }
            });

            if (hasMemberShip == 1) { // has membership
                holder.userImg.setBackgroundResource(0);
                Glide.with(mContext).load(Constants.BASE_IMAGE_URL + mUserList.get(position).getUserPic()).apply(options).into(holder.userImg);
                holder.uNameTxt.setText(mUserList.get(position).getFirstName());
//                holder.uAgeTxt.setText((mYear - mUserList.get(position).getBirthYear()) + "");
                holder.uAgeTxt.setText("");
                holder.uLocTxt.setText(mUserList.get(position).getCity());
                holder.uLastVisit.setText(mUserList.get(position).getActiveDate());
                holder.dataLayout.setBackgroundResource(0);
                holder.optionsLayout.setVisibility(View.VISIBLE);
            } else {
                holder.userImg.setBackgroundResource(R.drawable.bg_round_light_gray);
//            userImg.setAlpha((float) 0.3);
                holder.uNameTxt.setText("");
                holder.uAgeTxt.setText("");
                holder.uLocTxt.setText("");
                holder.uLastVisit.setText("");
                holder.dataLayout.setBackgroundColor(mContext.getResources().getColor(R.color.divider_color));
                holder.optionsLayout.setVisibility(View.GONE);
            }
            holder.uAgeTxt.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (mUserList == null) {
            return 0;
        }
        if (isLastPage)
            return mUserList.size() + 1;

        return mUserList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isLastPage && position == mUserList.size()) {
            return TYPE_FOOTER;
        }

        return super.getItemViewType(position);
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout userAction, optionsLayout, dataLayout, bgImg;
        private ImageView userImg, uMsgImg, uLikeImg, uFavImg;
        private TextView uStatus, uNameTxt, uLocTxt, uAgeTxt, uLastVisit;

        public ItemViewHolder(View mView) {
            super(mView);
            userAction = (LinearLayout) mView.findViewById(R.id.user_action);
            optionsLayout = (LinearLayout) mView.findViewById(R.id.options);
            dataLayout = (LinearLayout) mView.findViewById(R.id.data_layout);
            bgImg = (LinearLayout) mView.findViewById(R.id.bg_img);
            userImg = (ImageView) mView.findViewById(R.id.u_img);
            uMsgImg = (ImageView) mView.findViewById(R.id.u_msg_img);
            uLikeImg = (ImageView) mView.findViewById(R.id.u_like_img);
            uFavImg = (ImageView) mView.findViewById(R.id.u_fav_img);

            uStatus = (TextView) mView.findViewById(R.id.u_status);
            uNameTxt = (TextView) mView.findViewById(R.id.u_name);
            uLocTxt = (TextView) mView.findViewById(R.id.u_loc);
            uAgeTxt = (TextView) mView.findViewById(R.id.u_age);
            uLastVisit = (TextView) mView.findViewById(R.id.u_last_visit);

            uNameTxt.setTypeface(KenbieApplication.S_SEMI_BOLD);
            uLocTxt.setTypeface(KenbieApplication.S_NORMAL);
            uAgeTxt.setTypeface(KenbieApplication.S_NORMAL);
            uLastVisit.setTypeface(KenbieApplication.S_NORMAL);
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
