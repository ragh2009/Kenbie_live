package com.kenbie.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.listeners.MsgUserActionListeners;
import com.kenbie.model.MsgUserItem;
import com.kenbie.util.Constants;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by rajaw on 7/26/2020.
 */

public class MsgUserRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_FOOTER = 1;
    private Context mContext;
    private ArrayList<MsgUserItem> mUserList;
    private ArrayList<MsgUserItem> oUserList;
    private LayoutInflater mLayoutInflater;
    private MsgUserActionListeners mActionListeners;
    private int mType;
    private RequestOptions options;
    private ItemFilter mFilter = new ItemFilter();
    private SharedPreferences mPref;
    private String bTitle = "";
    private boolean isLastPage;

    public MsgUserRecycleViewAdapter(Context context, ArrayList<MsgUserItem> userList, MsgUserActionListeners msgUserActionListeners, int type) {
        mContext = context;
        mUserList = userList;
        oUserList = userList;
        mLayoutInflater = LayoutInflater.from(mContext);
        mActionListeners = msgUserActionListeners;
        mPref = mContext.getSharedPreferences("kPrefs", MODE_PRIVATE);
        bTitle = mPref.getString("56", "You've reached the end of the list");
        mType = type;
        if (mUserList == null)
            mUserList = new ArrayList<>();
        if (oUserList == null)
            oUserList = new ArrayList<>();
        options = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.img_c_user_dummy)
                .priority(Priority.HIGH);
    }


    public void refreshData(ArrayList<MsgUserItem> userList) {
        mUserList = userList;
        if (mUserList == null)
            mUserList = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void refreshFromData(ArrayList<MsgUserItem> userList) {
        mUserList = userList;
        if (mUserList == null)
            mUserList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TYPE_FOOTER) {
            //Inflating footer view
            itemView = mLayoutInflater.inflate(R.layout.footer_view, parent, false);
            return new FooterViewHolder(itemView);
        }

        itemView = mLayoutInflater.inflate(R.layout.msg_user_cell_view, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        if (mUserList == null) {
            return 0;
        }
        if (isLastPage)
            return mUserList.size() + 1;

        return mUserList.size();
//        return castingUsers.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isLastPage && position == mUserList.size()) {
            return TYPE_FOOTER;
        }

        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
        if (holder1 instanceof FooterViewHolder) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder1;
            footerHolder.footerText.setText(bTitle);
        } else if (holder1 instanceof ItemViewHolder) {
            ItemViewHolder holder = (ItemViewHolder) holder1;
            if (mUserList.get(position).getCurrent_status() == 1)
                holder.uStatus.setBackgroundResource(R.drawable.bg_round_green);
            else
                holder.uStatus.setBackgroundResource(R.drawable.bg_round_gray);

            holder.unreadCount.setText(mUserList.get(position).getNew_msg_count() + "");
            if (mUserList.get(position).getNew_msg_count() > 0)
                holder.unreadCount.setVisibility(View.VISIBLE);
            else
                holder.unreadCount.setVisibility(View.GONE);

            holder.uFavStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActionListeners.updateFavStatus(1, position);
                }
            });

            if (mUserList.get(position).getIsFav() == 1)
                holder.favImg.setBackgroundResource(R.drawable.ic_m_fav);
            else
                holder.favImg.setBackgroundResource(R.drawable.ic_unfav);

            holder.userAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActionListeners.updateFavStatus(3, position);
//                mActionListeners.userConStart(mUserList.get(position));
                }
            });

            holder.chatDeleteBtn.setText(mPref.getString("343", "Delete"));
            holder.chatDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActionListeners.updateFavStatus(2, position);
                }
            });

            if (mType == 1) { // Has membership
                holder.userImg.setBackgroundResource(0);
                holder.userName.setBackgroundResource(0);
                Glide.with(mContext).load(Constants.BASE_IMAGE_URL + mUserList.get(position).getUser_img()).apply(options).into(holder.userImg);
                holder.userName.setText(mUserList.get(position).getUser_name());

                if (mUserList.get(position).getUid() == 1)
                    holder.uFavStatus.setVisibility(View.GONE);
                else
                    holder.uFavStatus.setVisibility(View.VISIBLE);
            } else {
//            Glide.with(mContext).load(Constants.BASE_IMAGE_URL + mUserList.get(position).getUser_img())
//                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(mContext, 25))).circleCrop()
//                    .into(userImg);
//            Glide.with(mContext).load(Constants.BASE_IMAGE_URL + mUserList.get(position).getUser_img()).apply(options).into(userImg);
//            userImg.setAlpha((float) 0.8);
                holder.userImg.setBackgroundResource(R.drawable.bg_round_light_gray);
                holder.userName.setBackgroundColor(mContext.getResources().getColor(R.color.divider_color));
                holder.uFavStatus.setVisibility(View.GONE);
            }
        }
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<MsgUserItem> list = oUserList;
            int count = list.size();
            final ArrayList<MsgUserItem> sList = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                if (list.get(i).getUser_name().toLowerCase().startsWith(filterString)) {
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
            mUserList = (ArrayList<MsgUserItem>) results.values;
            notifyDataSetChanged();
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView userImg, favImg;
        private TextView uStatus, unreadCount, userName, chatDeleteBtn;
        private LinearLayout uFavStatus, userAction;

        public ItemViewHolder(View view) {
            super(view);
            userImg = (ImageView) view.findViewById(R.id.u_img);
            uStatus = (TextView) view.findViewById(R.id.u_status);
            unreadCount = (TextView) view.findViewById(R.id.u_unread_count);
            unreadCount.setTypeface(KenbieApplication.S_NORMAL);
            userName = (TextView) view.findViewById(R.id.u_name);
            userName.setTypeface(KenbieApplication.S_BOLD);
            uFavStatus = (LinearLayout) view.findViewById(R.id.u_fav_status);
            favImg = (ImageView) view.findViewById(R.id.u_fav_img);
            userAction = (LinearLayout) view.findViewById(R.id.user_action);
            chatDeleteBtn = (TextView) view.findViewById(R.id.btn_delete_chat);
            chatDeleteBtn.setTypeface(KenbieApplication.S_NORMAL);
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



