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

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.listeners.UserActionListener;
import com.kenbie.model.UserItem;
import com.kenbie.util.Constants;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by rajaw on 02/06/2019.
 */

public class UserGridDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_ITEM = 2;
    private ArrayList<UserItem> mUserData;
    private UserActionListener myListeners;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int mYear, hasMemberShip;
    private RequestOptions options = null;
    private boolean isLastPage;
    private String bTitle = "";
    private int paddingDimen = 1;

    public UserGridDataAdapter(Context context, ArrayList<UserItem> userItemArrayList, UserActionListener mListeners, boolean lPage) {
        SharedPreferences mPref = context.getSharedPreferences("kPrefs", MODE_PRIVATE);
        hasMemberShip = mPref.getInt("MemberShip", 0);
        bTitle = mPref.getString("56", "You've reached the end of the list");

        this.mUserData = userItemArrayList;
        this.mContext = context;
        this.isLastPage = lPage;
        paddingDimen = (int) mContext.getResources().getDimension(R.dimen.size_1);
        mLayoutInflater = LayoutInflater.from(mContext);
        this.myListeners = mListeners;
        if (mUserData == null)
            mUserData = new ArrayList<>();
        Calendar mCalendar = Calendar.getInstance();
        mYear = mCalendar.get(Calendar.YEAR);
        options = new RequestOptions()
                .placeholder(R.drawable.no_img)
                .priority(Priority.HIGH);
//                .transforms(new CenterCrop(), new RoundedCorners(24));
    }

    public void refreshData(ArrayList<UserItem> userList, boolean lPage) {
        this.mUserData = userList;
        this.isLastPage = lPage;
        if (mUserData == null)
            mUserData = new ArrayList<>();
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

        itemView = mLayoutInflater.inflate(R.layout.user_list_item, parent, false);
        return new ItemViewHolder(itemView);

  /*      // create a new view
        View v = mLayoutInflater.inflate(R.layout.user_list_item, parent, false);

        return new ViewHolder(v);*/
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
        if (holder1 instanceof FooterViewHolder) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder1;
            footerHolder.footerText.setText(bTitle);
        } else if (holder1 instanceof ItemViewHolder) {
            ItemViewHolder holder = (ItemViewHolder) holder1;
            if (mUserData.get(position).getIsActive() == 0) {
                holder.userView.setContentPadding(2, 2, 2, 2);
                holder.userView.setCardBackgroundColor(mContext.getResources().getColor(R.color.red_g_color));
//                holder.userView.setContentPadding(paddingDimen, paddingDimen, paddingDimen, paddingDimen);
//            holder.userView.setBackgroundResource(R.drawable.bg_white_outer_red_style);
            } else {
                holder.userView.setContentPadding(0, 0, 0, 0);
                holder.userView.setCardBackgroundColor(mContext.getResources().getColor(R.color.white));

//            llOutline.setPadding(2,4,4,2);
//            holder.userView.setBackgroundResource(R.drawable.bg_white_outer_light_gray_style);
//            holder.userView.setBackgroundResource(R.drawable.bg_white_outer_light_gray_style);
            }

//        holder.llTransparent.setBackgroundColor(Utility.getColorWithAlpha(Color.TRANSPARENT, 0.2f));
            holder.llTransparent.setBackgroundResource(R.drawable.overlay_profiles);

            holder.userPhotoTxt.setText(mUserData.get(position).getTotalImage() + "");
            holder.userPhotoTxt.setTypeface(KenbieApplication.S_NORMAL);

            String userData = "", firstName = "";
            firstName = mUserData.get(position).getFirstName();
            if (firstName != null && firstName.length() > 14)
                firstName = firstName.substring(0, 14);
            else
                firstName = mUserData.get(position).getFirstName();

//            if (mUserData.get(position).getIsActive() == 1) {
//                userData = userData + "<font color=#404040>" + firstName + ", " + (mYear - mUserData.get(position).getBirthYear()) + "</font>";
//            } else {
//                userData = userData + "<font color=#404040>" + firstName + ", " + (mYear - mUserData.get(position).getBirthYear()) + "</font>";
//            }
            if (mUserData.get(position).getBirthYear() != 0)
                userData = userData + "<font color=#404040>" + firstName + "</font>";
            else if (mUserData.get(position).getIsActive() == 1)
                userData = userData + "<font color=#404040>" + firstName + "</font>";
            else
                userData = userData + "<font color=#404040>" + firstName + "</font>";

            holder.uNameTxt.setTypeface(KenbieApplication.S_SEMI_BOLD);

            String location = "";

            if (mUserData.get(position).getCity() != null && !mUserData.get(position).getCity().equalsIgnoreCase("null"))
                location = mUserData.get(position).getCity();
            else
                location = "";

            if (mUserData.get(position).getCountry() != null && !mUserData.get(position).getCountry().equalsIgnoreCase("null")) {
                if (location.length() > 3)
                    location = location + ", " + mUserData.get(position).getCountry();
                else
                    location = mUserData.get(position).getCountry();
            } else
                location = location + "";

            holder.uLocationTxt.setTypeface(KenbieApplication.S_NORMAL);


            holder.userView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myListeners.getUserAction(mUserData.get(holder.getAdapterPosition()), holder.getAdapterPosition());
                }
            });

            if (hasMemberShip == 1) { // has membership
                holder.userImg.setBackgroundResource(0);
                Glide.with(mContext)
                        .load(Constants.BASE_IMAGE_URL + mUserData.get(position).getUserPic()).apply(options)
                        .into(holder.userImg);
                holder.uNameTxt.setText(Html.fromHtml(userData), TextView.BufferType.SPANNABLE);
                holder.uLocationTxt.setText(location);
                holder.uNameTxt.setBackgroundColor(0);
                holder.uLocationTxt.setBackgroundColor(0);
//                holder.llTransparent.setBackgroundColor(Utility.getColorWithAlpha(Color.BLACK, 0.2f));
            } else {
                holder.userImg.setBackgroundResource(R.drawable.gray_bg_no_outer);
                holder.uNameTxt.setText("");
                holder.uLocationTxt.setText("");
                holder.uNameTxt.setBackgroundColor(mContext.getResources().getColor(R.color.divider_color));
                holder.uLocationTxt.setBackgroundColor(mContext.getResources().getColor(R.color.divider_color));
//                holder.llTransparent.setBackgroundColor(mContext.getResources().getColor(R.color.divider_color));
            }
        }
    }


    @Override
    public int getItemCount() {
        if (mUserData == null) {
            return 0;
        }
        if (isLastPage)
            return mUserData.size() + 1;

        return mUserData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isLastPage && position == mUserData.size()) {
            return TYPE_FOOTER;
        }

        return super.getItemViewType(position);
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private ImageView userImg;
        private TextView userPhotoTxt, uNameTxt, uLocationTxt;
        private LinearLayout llTransparent;
        private CardView userView;

        public ItemViewHolder(View mView) {
            super(mView);
            userImg = (ImageView) mView.findViewById(R.id.u_image);
            llTransparent = (LinearLayout) mView.findViewById(R.id.ll_transparent);
            userPhotoTxt = (TextView) mView.findViewById(R.id.u_photo);
            uNameTxt = (TextView) mView.findViewById(R.id.u_name);
            uLocationTxt = (TextView) mView.findViewById(R.id.u_location);
            userView = (CardView) mView.findViewById(R.id.user_cv);
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
