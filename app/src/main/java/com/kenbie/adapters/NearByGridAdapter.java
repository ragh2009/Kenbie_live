package com.kenbie.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.model.UserItem;
import com.kenbie.util.Constants;

import java.util.ArrayList;

/**
 * Created by rajaw on 10/18/2017.
 */

public class NearByGridAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<UserItem> mUserList;
    private LayoutInflater mLayoutInflater;

    public NearByGridAdapter(Context context, ArrayList<UserItem> userList) {
        mContext = context;
        mUserList = userList;
        mLayoutInflater = LayoutInflater.from(mContext);
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
            convertView = mLayoutInflater.inflate(R.layout.near_by_grid_item, null);
        else ;

        ImageView userImg = (ImageView) convertView.findViewById(R.id.user_image);
        Glide.with(mContext).load(Constants.BASE_IMAGE_URL + mUserList.get(position).getUserPic()).apply(RequestOptions.circleCropTransform()).into(userImg);

        TextView uNameTxt = (TextView) convertView.findViewById(R.id.user_name);
        uNameTxt.setText(mUserList.get(position).getFirstName());
        uNameTxt.setTypeface(KenbieApplication.S_SEMI_BOLD);

        return convertView;
    }
}
