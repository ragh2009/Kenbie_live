package com.kenbie.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.kenbie.views.BaseViewHolder;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by rajaw on 02/06/2019.
 */

public class ModelsDataAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    private ArrayList<UserItem> mUserData;
    private UserActionListener myListeners;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int mType;
    private RequestOptions options = null;


    public ModelsDataAdapter(Context context, ArrayList<UserItem> userItemArrayList, UserActionListener mListeners, int type) {
        this.mUserData = userItemArrayList;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.myListeners = mListeners;
        if (mUserData == null)
            mUserData = new ArrayList<>();
        Calendar mCalendar = Calendar.getInstance();
        mType = type;
        options = new RequestOptions()
                .placeholder(R.drawable.no_img)
                .priority(Priority.HIGH);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.user_new_item, parent, false));
            case VIEW_TYPE_LOADING:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == mUserData.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return mUserData == null ? 0 : mUserData.size();
    }

    public void addItems(ArrayList<UserItem> userItemArrayList) {
        mUserData.addAll(userItemArrayList);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        mUserData.add(new UserItem());
        notifyItemInserted(mUserData.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = mUserData.size() - 1;
        UserItem item = getItem(position);
        if (item != null) {
            mUserData.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        mUserData.clear();
        notifyDataSetChanged();
    }

    UserItem getItem(int position) {
        return mUserData.get(position);
    }

    public class ViewHolder extends BaseViewHolder {
        ImageView userImg;
        TextView userPhotoTxt;
        TextView uNameTxt;
        TextView uLocationTxt;
        LinearLayout llTransparent;
        CardView userView;

        ViewHolder(View mView) {
            super(mView);
            userImg = (ImageView) mView.findViewById(R.id.u_image);
            llTransparent = (LinearLayout) mView.findViewById(R.id.ll_transparent);
            userPhotoTxt = (TextView) mView.findViewById(R.id.u_photo);
            uNameTxt = (TextView) mView.findViewById(R.id.u_name);
            uLocationTxt = (TextView) mView.findViewById(R.id.u_location);
            userView = (CardView) mView.findViewById(R.id.user_cv);
        }

        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            UserItem item = mUserData.get(position);

            Glide.with(mContext)
                    .load(Constants.BASE_IMAGE_URL + item.getUserPic()).apply(options)
                    .into(userImg);

            llTransparent.setBackgroundColor(Utility.getColorWithAlpha(Color.BLACK, 0.2f));

            userPhotoTxt.setText(item.getTotalImage() + "");
            userPhotoTxt.setTypeface(KenbieApplication.S_NORMAL);

            String userData = "", firstName = "";
            firstName = item.getFirstName();
            if (firstName != null && firstName.length() > 14)
                firstName = firstName.substring(0, 14);
            else
                firstName = item.getFirstName();

            if (item.getBirthYear() != 0)
                if (item.getIsActive() == 1)
                    userData = userData + "<font color=#404040>" + firstName + ", " + item.getBirthYear() + "</font><font color=#00FF00><b>" + " ." + "</b></font>";
                else
                    userData = userData + "<font color=#404040>" + firstName + ", " + item.getBirthYear() + "</font>";
            else if (item.getIsActive() == 1)
                userData = userData + "<font color=#404040>" + firstName + "</font><font color=#00FF00><b>" + " ." + "</b></font>";
            else
                userData = userData + "<font color=#404040>" + firstName + "</font>";

            uNameTxt.setText(Html.fromHtml(userData), TextView.BufferType.SPANNABLE);
            uNameTxt.setTypeface(KenbieApplication.S_SEMI_BOLD);

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

            uLocationTxt.setText(location);
            uLocationTxt.setTypeface(KenbieApplication.S_NORMAL);


            userView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myListeners.getUserAction(mUserData.get(position), mUserData.get(position).getType());
                }
            });
        }
    }
}
