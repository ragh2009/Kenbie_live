package com.kenbie.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kenbie.KenbieActivity;
import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.fragments.UserListFragment;
import com.kenbie.listeners.InfoListener;
import com.kenbie.listeners.UserActionListener;
import com.kenbie.model.UserItem;
import com.kenbie.model.UserTypeData;
import com.kenbie.util.SpacesItemDecoration;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by rajaw on 02/06/2019.
 */

public class UserDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_ITEM = 2;
    private ArrayList<UserTypeData> mUserData;
    private UserActionListener myListeners;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private SharedPreferences mPref = null;
    private String modelTitle = "", photographerTitle = "", agencyTitle = "";
    private boolean isLastPage;
    private String bTitle = "";

    public UserDataAdapter(Context context, ArrayList<UserTypeData> userItemArrayList, UserActionListener mListeners, boolean lPage) {
        this.mUserData = userItemArrayList;
        this.mContext = context;
        this.isLastPage = lPage;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.myListeners = mListeners;
        mPref = mContext.getSharedPreferences("kPrefs", MODE_PRIVATE);
        modelTitle = mPref.getString("315", "Show all Models");
        photographerTitle = mPref.getString("317", "Show all Photographers");
        agencyTitle = mPref.getString("318", "Show all Agencies");
        bTitle = mPref.getString("56", "You've reached the end of the list");

        if (mUserData == null)
            mUserData = new ArrayList<>();
    }

    public void addItems(ArrayList<UserTypeData> userItemArrayList, boolean lPage) {
        this.mUserData = userItemArrayList;
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

        itemView = mLayoutInflater.inflate(R.layout.main_view_cell_item, parent, false);
        return new ItemViewHolder(itemView);

        // create a new view
//        View v = mLayoutInflater.inflate(R.layout.main_view_cell_item, parent, false);
//        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
        if (holder1 instanceof FooterViewHolder) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder1;
            footerHolder.footerText.setText(bTitle);
        } else if (holder1 instanceof ItemViewHolder) {
            ItemViewHolder holder = (ItemViewHolder) holder1;

            holder.title.setText(mUserData.get(position).getType());

//        holder.childLayout.setHasFixedSize(true);
            if (mUserData.get(position).getdType() == 3) {
                holder.childLayout.setVisibility(View.VISIBLE);
                holder.childLayout.setHasFixedSize(true);
                holder.childHorzLayout.setVisibility(View.GONE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false);
                holder.childLayout.setLayoutManager(linearLayoutManager);
                SponsorDataAdapter sponsorAdapter = new SponsorDataAdapter(mContext, mUserData.get(position).getUserItems(), myListeners);
                holder.childLayout.setAdapter(sponsorAdapter);
            } else if (mUserData.get(position).getdType() == 1) {
                holder.childLayout.setVisibility(View.VISIBLE);
                holder.childLayout.setHasFixedSize(true);
                holder.childHorzLayout.setVisibility(View.GONE);
                holder.childLayout.setLayoutManager(new GridLayoutManager(mContext, 2));
                UserChildDataAdapter userAdapter = new UserChildDataAdapter(mContext, mUserData.get(position).getUserItems(), myListeners, mUserData.get(position).getdType());
                holder.childLayout.setAdapter(userAdapter);
//            int spacingInPixels = mContext.getResources().getDimensionPixelSize(R.dimen.size_10);
//            holder.childLayout.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
            } else {
                holder.childLayout.setVisibility(View.GONE);
                holder.childHorzLayout.setVisibility(View.VISIBLE);
                holder.childHorzLayout.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false);
                holder.childHorzLayout.setLayoutManager(linearLayoutManager);
                UserChildDataAdapter userAdapter = new UserChildDataAdapter(mContext, mUserData.get(position).getUserItems(), myListeners, mUserData.get(position).getdType());
                holder.childHorzLayout.setAdapter(userAdapter);
//            holder.childLayout.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
            }

//        1 - models, 2 - photographer, 3 - sponsored, 4 - agency
            if (mUserData.get(position).getdType() == 1) {
                holder.viewAll.setVisibility(View.VISIBLE);
                holder.viewAll.setText(modelTitle + " >");
            } else if (mUserData.get(position).getdType() == 2) {
                holder.viewAll.setVisibility(View.VISIBLE);
                holder.viewAll.setText(photographerTitle + " >");
            } else if (mUserData.get(position).getdType() == 4) {
                holder.viewAll.setVisibility(View.VISIBLE);
                holder.viewAll.setText(agencyTitle + " >");
            } else
                holder.viewAll.setVisibility(View.INVISIBLE);

            holder.viewAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mUserData.get(holder.getAdapterPosition()).getdType() == 1)
                        myListeners.getUserAction(null, 5);
                    else if (mUserData.get(holder.getAdapterPosition()).getdType() == 2)
                        myListeners.getUserAction(null, 6);
                    else if (mUserData.get(holder.getAdapterPosition()).getdType() == 4)
                        myListeners.getUserAction(null, 7);
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
        // each data item is just a string in this case ItemViewHolder
        private TextView title, viewAll;
        private RecyclerView childLayout, childHorzLayout;

        public ItemViewHolder(View mView) {
            super(mView);
            title = (TextView) mView.findViewById(R.id.title);
            title.setTypeface(KenbieApplication.S_BOLD);
            viewAll = (TextView) mView.findViewById(R.id.view_all);
            viewAll.setTypeface(KenbieApplication.S_SEMI_BOLD);
            childLayout = (RecyclerView) mView.findViewById(R.id.child_layout);
            childHorzLayout = (RecyclerView) mView.findViewById(R.id.child_horz_layout);
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
