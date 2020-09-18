package com.kenbie.adapters;


import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.model.UserItem;
import com.kenbie.util.Constants;
import com.kenbie.util.Utility;

import java.util.ArrayList;
import java.util.Calendar;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by rajaw on 7/27/2017.
 */

public class UserListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<UserItem> mUserList;
    private LayoutInflater mLayoutInflater;
    private int mYear;
    private RequestOptions options = null;

    public UserListAdapter(Context context, ArrayList<UserItem> userList) {
        mContext = context;
        mUserList = userList;
        if (mUserList == null)
            mUserList = new ArrayList<>();
        mLayoutInflater = LayoutInflater.from(mContext);
        Calendar mCalendar = Calendar.getInstance();
        mYear = mCalendar.get(Calendar.YEAR);
        options = new RequestOptions()
                .placeholder(R.drawable.no_img)
                .priority(Priority.HIGH);
//                .bitmapTransform(new RoundedCornersTransformation(mContext, 8, 0, RoundedCornersTransformation.CornerType.TOP));
    }

    public int getCount() {
        return mUserList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = mLayoutInflater.inflate(R.layout.user_list_item, null);
        else ;

        ImageView userImg = (ImageView) convertView.findViewById(R.id.u_image);
//        Glide.with(mContext)
//                .load(Constants.BASE_IMAGE_URL + mUserList.get(position).getUserPic()).apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(mContext, 8, 0, RoundedCornersTransformation.CornerType.TOP))).placeholder(R.drawable.no_img).error(R.drawable.no_img)
//                .into(userImg);

        Glide.with(mContext)
                .load(Constants.BASE_IMAGE_URL + mUserList.get(position).getUserPic()).apply(options)
                .into(userImg);

      /*  CardView llOutline = (CardView) convertView.findViewById(R.id.user_cv); // setBirthMonth
        if (mUserList.get(position).getIsActive() == 0) {
//            llOutline.setPadding(2,4,4,2);
            llOutline.setBackgroundResource(R.drawable.bg_white_outer_red_style);
        }else {
//            llOutline.setPadding(2,4,4,2);
            llOutline.setBackgroundResource(R.drawable.bg_white_outer_light_gray_style);
        }*/

        LinearLayout llTransparent = (LinearLayout) convertView.findViewById(R.id.ll_transparent);
        llTransparent.setBackgroundColor(Utility.getColorWithAlpha(Color.BLACK, 0.2f));

        TextView userPhotoTxt = (TextView) convertView.findViewById(R.id.u_photo);
        userPhotoTxt.setText(mUserList.get(position).getTotalImage() + "");
        userPhotoTxt.setTypeface(KenbieApplication.S_NORMAL);

/*        TextView userStatusTxt = (TextView) convertView.findViewById(R.id.u_status);
        if (mUserList.get(position).getIsActive() == 1) {
            userStatusTxt.setText(mContext.getString(R.string.online_title));
            userStatusTxt.setTextColor(mContext.getResources().getColor(R.color.green_color));
        } else {
            userStatusTxt.setText(mContext.getString(R.string.offline_title));
            userStatusTxt.setTextColor(mContext.getResources().getColor(R.color.white));
        }
        userStatusTxt.setTypeface(KenbieApplication.S_NORMAL);*/

        TextView uNameTxt = (TextView) convertView.findViewById(R.id.u_name);

        String userData = "";
        if (mUserList.get(position).getBirthYear() != 0)
            if (mUserList.get(position).getIsActive() == 1)
                userData = userData + "<font color=#404040>" + mUserList.get(position).getFirstName() + ", " + (mYear - mUserList.get(position).getBirthYear()) + "</font><font color=#00FF00><b>" + " ." + "</b></font>";
            else
                userData = userData + "<font color=#404040>" + mUserList.get(position).getFirstName() + ", " + (mYear - mUserList.get(position).getBirthYear()) + "</font>";
        else if (mUserList.get(position).getIsActive() == 1)
            userData = userData + "<font color=#404040>" + mUserList.get(position).getFirstName() + "</font><font color=#00FF00><b>" + " ." + "</b></font>";
        else
            userData = userData + "<font color=#404040>" + mUserList.get(position).getFirstName() + "</font>";

        uNameTxt.setText(Html.fromHtml(userData), TextView.BufferType.SPANNABLE);
        uNameTxt.setTypeface(KenbieApplication.S_NORMAL);

        TextView uLocationTxt = (TextView) convertView.findViewById(R.id.u_location);
        String location = "";

        if (mUserList.get(position).getCity() != null && !mUserList.get(position).getCity().equalsIgnoreCase("null"))
            location = mUserList.get(position).getCity();
        else
            location = "";

        if (mUserList.get(position).getCountry() != null && !mUserList.get(position).getCountry().equalsIgnoreCase("null")) {
            if (location.length() > 3)
                location = location + ", " + mUserList.get(position).getCountry();
            else
                location = mUserList.get(position).getCountry();
        } else
            location = location + "";

        uLocationTxt.setText(location);
        uLocationTxt.setTypeface(KenbieApplication.S_SEMI_LIGHT);

        return convertView;
    }
}

