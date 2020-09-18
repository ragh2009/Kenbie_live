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

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.fragments.UserGridFragment;
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

public class VisitorUserGridAdapter extends BaseAdapter {
    private RequestOptions options;
    private Context mContext;
    private ArrayList<UserItem> mUserList;
    private LayoutInflater mLayoutInflater;
    private MsgUserActionListeners msgUserActionListeners;
    private int mYear, hasMemberShip;

    public VisitorUserGridAdapter(Context context, ArrayList<UserItem> userList, MsgUserActionListeners msgUserActionListeners) {
        SharedPreferences mPref = context.getSharedPreferences("kPrefs", MODE_PRIVATE);
        hasMemberShip =  mPref.getInt("MemberShip", 0);
        mContext = context;
        mUserList = userList;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.msgUserActionListeners = msgUserActionListeners;
        Calendar mCalendar = Calendar.getInstance();
        mYear = mCalendar.get(Calendar.YEAR);
        options = new RequestOptions()
                .circleCrop()
                .placeholder(mContext.getResources().getDrawable(R.drawable.img_c_user_dummy))
                .priority(Priority.HIGH);
    }

    public void refreshData(ArrayList<UserItem> userList) {
        mUserList = userList;
        if (mUserList == null)
            mUserList = new ArrayList<>();
        notifyDataSetChanged();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = mLayoutInflater.inflate(R.layout.visitor_user_cell_item, null);
        else ;

        LinearLayout optionsLayout = (LinearLayout)convertView.findViewById(R.id.options);
        LinearLayout dataLayout = (LinearLayout)convertView.findViewById(R.id.data_layout);
        LinearLayout bgImg = (LinearLayout)convertView.findViewById(R.id.bg_img);
        if(mUserList.get(position).getBirthMonth() == 0)
            bgImg.setBackgroundResource(R.drawable.bg_round_red);
        else
            bgImg.setBackgroundResource(0);
        ImageView userImg = (ImageView) convertView.findViewById(R.id.u_img);

        TextView uStatus = (TextView) convertView.findViewById(R.id.u_status);
        if (mUserList.get(position).getIsActive() == 1)
            uStatus.setBackgroundResource(R.drawable.bg_round_green);
        else
            uStatus.setBackgroundResource(R.drawable.bg_round_gray);

        TextView uNameTxt = (TextView) convertView.findViewById(R.id.u_name);
        uNameTxt.setTypeface(KenbieApplication.S_SEMI_BOLD);

        TextView uLocTxt = (TextView) convertView.findViewById(R.id.u_loc);
        uLocTxt.setTypeface(KenbieApplication.S_NORMAL);

        TextView uAgeTxt = (TextView) convertView.findViewById(R.id.u_age);
        uAgeTxt.setTypeface(KenbieApplication.S_NORMAL);

        TextView uLastVisit = (TextView) convertView.findViewById(R.id.u_last_visit);
        uLastVisit.setTypeface(KenbieApplication.S_NORMAL);

        ImageView uMsgImg = (ImageView) convertView.findViewById(R.id.u_msg_img);
        ImageView uLikeImg = (ImageView) convertView.findViewById(R.id.u_like_img);
        ImageView uFavImg = (ImageView) convertView.findViewById(R.id.u_fav_img);

        if (mUserList.get(position).getBirthMonth() == 1)
            uMsgImg.setBackgroundResource(R.drawable.ic_v_message_unread);
        else
            uMsgImg.setBackgroundResource(R.drawable.ic_v_message_read);

        if (mUserList.get(position).getType() == 1)
            uLikeImg.setBackgroundResource(R.drawable.ic_v_like_selected);
        else
            uLikeImg.setBackgroundResource(R.drawable.ic_v_like_pv);

        if (mUserList.get(position).getBirthDay() == 1)
            uFavImg.setBackgroundResource(R.drawable.ic_v_favourite_selected);
        else
            uFavImg.setBackgroundResource(R.drawable.ic_v_favourite_pv);

        uMsgImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MsgUserItem msgUserItem = new MsgUserItem();
                msgUserItem.setUid(mUserList.get(position).getId());
                msgUserItem.setUser_name(mUserList.get(position).getFirstName());
                msgUserItem.setUser_img(mUserList.get(position).getUserPic());
                msgUserActionListeners.userConStart(msgUserItem);
            }
        });

        uLikeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mUserList.get(position).setType(mUserList.get(position).getType() == 1 ? 0 : 1);
                msgUserActionListeners.updateFavStatus(1, position);
//                notifyDataSetChanged();
            }
        });

        uFavImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mUserList.get(position).setBirthDay(mUserList.get(position).getBirthDay() == 1 ? 0 : 1);
                msgUserActionListeners.updateFavStatus(2, position);
//                notifyDataSetChanged();
            }
        });

        if(hasMemberShip == 1){ // has membership
            userImg.setBackgroundResource(0);
            Glide.with(mContext).load(Constants.BASE_IMAGE_URL + mUserList.get(position).getUserPic()).apply(options).into(userImg);
            uNameTxt.setText(mUserList.get(position).getFirstName());
            uAgeTxt.setText((mYear - mUserList.get(position).getBirthYear()) + "");
            uLocTxt.setText(mUserList.get(position).getCity());
            uLastVisit.setText(mUserList.get(position).getActiveDate());
            dataLayout.setBackgroundResource(0);
            optionsLayout.setVisibility(View.VISIBLE);
        }else{
            userImg.setBackgroundResource(R.drawable.bg_round_light_gray);
//            userImg.setAlpha((float) 0.3);
            uNameTxt.setText("");
            uAgeTxt.setText("");
            uLocTxt.setText("");
            uLastVisit.setText("");
            dataLayout.setBackgroundColor(mContext.getResources().getColor(R.color.divider_color));
            optionsLayout.setVisibility(View.GONE);
          }

        return convertView;
    }
}
