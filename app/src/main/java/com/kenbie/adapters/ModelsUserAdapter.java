package com.kenbie.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.listeners.CastingUserListeners;
import com.kenbie.model.MsgUserItem;
import com.kenbie.util.Constants;
import com.kenbie.util.Utility;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by rajaw on 2/16/2019.
 */

public class ModelsUserAdapter extends RecyclerView.Adapter<ModelsUserAdapter.ViewHolder> {
    private ArrayList<MsgUserItem> modelUsers;
    private Context mContext;
    private CastingUserListeners mListeners;
    private Utility utility;
    private int type;
    private String sendMessageTitle;

    public ModelsUserAdapter(Context context, ArrayList<MsgUserItem> modelUsers1, CastingUserListeners castingUserListeners, int type, String sendMsgTitle) {
        this.modelUsers = modelUsers1;
        this.mContext = context;
        this.mListeners = castingUserListeners;
        utility = new Utility();
        this.type = type;
        sendMessageTitle = sendMsgTitle;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.casting_model_cell_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ModelsUserAdapter.ViewHolder holder, int position) {
        Glide.with(mContext).load(Constants.BASE_IMAGE_URL + modelUsers.get(position).getUser_img()).apply(RequestOptions.circleCropTransform()).into(holder.mUserImg);

        holder.mName.setText(modelUsers.get(position).getUser_name());
        holder.mUserLoc.setText(modelUsers.get(position).getLast_response_time());

        holder.mHeight.setText("");

        holder.userAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListeners.getUserDetails(holder.getAdapterPosition(), 1);
            }
        });

        holder.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListeners.getUserDetails(holder.getAdapterPosition(), 2);
            }
        });
    }


    @Override
    public int getItemCount() {
        return modelUsers.size();
    }

    public void addItem(MsgUserItem castingUser) {
        modelUsers.add(castingUser);
        notifyItemInserted(modelUsers.size());
    }

    public void removeItem(int position) {
        modelUsers.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, modelUsers.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mUserImg;
        private TextView mName, mUserLoc, mHeight, sendMessage;
        private LinearLayout userAction;

        public ViewHolder(View view) {
            super(view);
            userAction = (LinearLayout) view.findViewById(R.id.user_action);
            mUserImg = (ImageView) view.findViewById(R.id.m_user_img);
            mName = (TextView) view.findViewById(R.id.m_name);
            mName.setTypeface(KenbieApplication.S_NORMAL);
            mUserLoc = (TextView) view.findViewById(R.id.m_user_loc);
            mUserLoc.setTypeface(KenbieApplication.S_SEMI_LIGHT);
            mHeight = (TextView) view.findViewById(R.id.m_height);
            mHeight.setTypeface(KenbieApplication.S_SEMI_LIGHT);
            sendMessage = (TextView) view.findViewById(R.id.send_msg_btn);
            sendMessage.setTypeface(KenbieApplication.S_SEMI_BOLD);
//            sendMessage.setText(sendMessageTitle);
        }
    }
}
