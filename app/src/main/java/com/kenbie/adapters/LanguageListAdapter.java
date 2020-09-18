package com.kenbie.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.listeners.MsgUserActionListeners;
import com.kenbie.model.MsgUserItem;
import com.kenbie.model.Option;
import com.kenbie.util.Constants;

import java.util.ArrayList;

/**
 * Created by rajaw on 1/12/2019
 */

public class LanguageListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Option> mLangList;
    private LayoutInflater mLayoutInflater;
    private MsgUserActionListeners mActionListeners;
    private int mSel;
    private RequestOptions options;

    public LanguageListAdapter(Context context, ArrayList<Option> mList, MsgUserActionListeners msgUserActionListeners, int selIndex) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mActionListeners = msgUserActionListeners;
        mLangList = mList;
        mSel = selIndex;
        if (mLangList == null)
            mLangList = new ArrayList<>();
        options = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.img_c_user_dummy)
                .priority(Priority.HIGH);
    }


    public void refreshData(int selIndex) {
        mSel = selIndex;
        notifyDataSetChanged();
    }

    public int getCount() {
        return mLangList.size();
    }

    public Object getItem(int position) {
        return mLangList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = mLayoutInflater.inflate(R.layout.language_cell_view, null);
        else ;

        TextView langName = (TextView) convertView.findViewById(R.id.lang_name);
        langName.setTypeface(KenbieApplication.S_NORMAL);
        langName.setText(mLangList.get(position).getTitle());

/*        LinearLayout uLangStatus = (LinearLayout) convertView.findViewById(R.id.u_lang_status);
        uLangStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionListeners.updateFavStatus(1, position);
            }
        });*/

        ImageView uLangImg = (ImageView) convertView.findViewById(R.id.u_lang_img);
        if (mSel == position)
            uLangImg.setBackgroundResource(R.drawable.r_selected);
        else
            uLangImg.setBackgroundResource(R.drawable.r_focus);

        return convertView;
    }
}



