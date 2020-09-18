package com.kenbie.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.kenbie.util.Utility;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by rajaw on 02/06/2019.
 */

public class AllModelsDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_FOOTER = 1;
    public final int TYPE_ITEM = 2;
    private ArrayList<UserItem> mUserData;
    private UserActionListener myListeners;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int mYear, mType;
    private RequestOptions options = null;
    private boolean isLastPage;
    private String bTitle = "";

    public AllModelsDataAdapter(Context context, ArrayList<UserItem> userItemArrayList, UserActionListener mListeners, int type, boolean lPage) {
        SharedPreferences mPref = context.getSharedPreferences("kPrefs", MODE_PRIVATE);
        bTitle = mPref.getString("56", "You've reached the end of the list");
        this.mUserData = userItemArrayList;
        this.mContext = context;
        this.isLastPage = lPage;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.myListeners = mListeners;
        if (mUserData == null)
            mUserData = new ArrayList<>();
        mType = type;
        options = new RequestOptions()
                .placeholder(R.drawable.no_img)
                .priority(Priority.HIGH);
    }

    public void refreshData(ArrayList<UserItem> userList, boolean lPage) {
        this.mUserData = userList;
        if (mUserData == null)
            mUserData = new ArrayList<>();
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

        itemView = mLayoutInflater.inflate(R.layout.user_new_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
        if (holder1 instanceof FooterViewHolder) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder1;
            footerHolder.footerText.setText(bTitle);
        } else if (holder1 instanceof ItemViewHolder) {
            ItemViewHolder holder = (ItemViewHolder) holder1;
            UserItem item = mUserData.get(position);

            Glide.with(mContext)
                    .load(Constants.BASE_IMAGE_URL + item.getUserPic()).apply(options)
                    .into(holder.userImg);

//            holder.llTransparent.setBackgroundColor(Utility.getColorWithAlpha(Color.BLACK, 0.2f));
            holder.llTransparent.setBackgroundResource(R.drawable.overlay_profiles);

            holder.userPhotoTxt.setText(item.getTotalImage() + "");
            holder.userPhotoTxt.setTypeface(KenbieApplication.S_NORMAL);

            String userData = "", firstName = "";
            firstName = item.getFirstName();
            if (firstName != null && firstName.length() > 14)
                firstName = firstName.substring(0, 14);
            else
                firstName = item.getFirstName();

            //                bundle.putInt("Type", 1); // Model
//                bundle.putInt("Type", 3); // Photographers
//                bundle.putInt("Type", 2); // Agency
            if (item.getBirthYear() != 0) {
                if (item.getIsActive() == 1)
                    holder.uStatus.setVisibility(View.VISIBLE);
                else
                    holder.uStatus.setVisibility(View.INVISIBLE);
                if (mType == 1 || mType == 4)
                    userData = userData + "<font color=#404040>" + firstName + ", " + item.getBirthYear() + "</font>";
                else
                    userData = userData + "<font color=#404040>" + firstName + "</font>";
            } else if (item.getIsActive() == 1) {
                holder.uStatus.setVisibility(View.VISIBLE);
                userData = userData + "<font color=#404040>" + firstName + "</font>";
//                userData = userData + "<font color=#404040>" + firstName + "</font><font color=#00FF00><b>" + " ." + "</b></font>";
            } else {
                holder.uStatus.setVisibility(View.INVISIBLE);
                userData = userData + "<font color=#404040>" + firstName + "</font>";
            }

            holder.uNameTxt.setText(Html.fromHtml(userData), TextView.BufferType.SPANNABLE);
            holder.uNameTxt.setTypeface(KenbieApplication.S_SEMI_BOLD);

            String location = "";

            if (item.getCity() != null && !item.getCity().equalsIgnoreCase("null"))
                location = item.getCity();
            else
                location = "";

            if (item.getCountry() != null && !item.getCountry().equalsIgnoreCase("null")) {
                if (location.length() > 3)
                    location = location + ", " + item.getCountry();
                else
                    location = item.getCountry();
            } else
                location = location + "";

            holder.uLocationTxt.setText(location);
            holder.uLocationTxt.setTypeface(KenbieApplication.S_NORMAL);


            holder.userView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myListeners.getUserAction(mUserData.get(position), mUserData.get(position).getType());
                }
            });
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
        private TextView userPhotoTxt, uNameTxt, uLocationTxt, uStatus;
        private LinearLayout llTransparent;
        private CardView userView;

        public ItemViewHolder(View mView) {
            super(mView);
            userImg = (ImageView) mView.findViewById(R.id.u_image);
            llTransparent = (LinearLayout) mView.findViewById(R.id.ll_transparent);
            userPhotoTxt = (TextView) mView.findViewById(R.id.u_photo);
            uNameTxt = (TextView) mView.findViewById(R.id.u_name);
            uStatus = (TextView) mView.findViewById(R.id.u_status);
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
