package com.kenbie.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.listeners.CastingUserListeners;
import com.kenbie.model.CastingUser;
import com.kenbie.model.UserItem;
import com.kenbie.util.Utility;
import com.kenbie.views.MySpannable;

import java.util.ArrayList;

/**
 * Created by rajaw on 9/21/2017.
 */

public class CastingUserNewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_ITEM = 2;
    private ArrayList<CastingUser> castingUsers;
    private Context mContext;
    private CastingUserListeners mListeners;
    private Utility utility;
    private int castingType = 0;
    private RequestOptions options = null;
    private SharedPreferences mPref;
    private LayoutInflater mLayoutInflater;
    private String bTitle = "";
    private boolean isLastPage;

    public CastingUserNewAdapter(Context context, ArrayList<CastingUser> castingUsers, CastingUserListeners castingUserListeners, int type, SharedPreferences mPref, boolean lPage) {
        this.castingUsers = castingUsers;
        this.mContext = context;
        this.mListeners = castingUserListeners;
        mLayoutInflater = LayoutInflater.from(mContext);
        utility = new Utility();
        this.castingType = type;
        this.mPref = mPref;
        this.isLastPage = lPage;
        bTitle = mPref.getString("56", "You've reached the end of the list");
        options = new RequestOptions()
                .optionalFitCenter()
                .placeholder(R.drawable.no_img)
                .priority(Priority.HIGH);
    }

    public void updateType(int cType) {
        castingType = cType;
    }

    public void refreshData(ArrayList<CastingUser> castingUsers, boolean lPage) {
        this.castingUsers = castingUsers;
        this.isLastPage = lPage;
        if (castingUsers == null)
            castingUsers = new ArrayList<>();
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

        itemView = mLayoutInflater.inflate(R.layout.casting_user_new_cell_view, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
        if (holder1 instanceof FooterViewHolder) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder1;
            footerHolder.footerText.setText(bTitle);
        } else if (holder1 instanceof ItemViewHolder) {
            ItemViewHolder holder = (ItemViewHolder) holder1;
            Glide.with(mContext).load(castingUsers.get(position).getCasting_img()).apply(options).into(holder.cUserImg);

            holder.cName.setText(castingUsers.get(position).getCasting_title());
            holder.cUserLoc.setText(castingUsers.get(position).getCasting_location());

            holder.cDates.setText(castingUsers.get(position).getCasting_start_date() + " " + mPref.getString("323", "to") + " " + castingUsers.get(position).getCasting_end_date());

            String more = castingUsers.get(position).getCasting_requirement();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.cDescription.setText(Html.fromHtml(more));
            } else
                holder.cDescription.setText(more);

      /*  if (more.length() > 40)
            try {
                makeTextViewResizable(holder.cDescription, 3, "Learn more", false);
            } catch (Exception e) {
                e.printStackTrace();
                holder.cDescription.setText(more);
            }
        else
            holder.cDescription.setText(more);*/

            holder.userAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListeners.getUserDetails(holder.getAdapterPosition(), castingType);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (castingUsers == null) {
            return 0;
        }
        if (isLastPage)
            return castingUsers.size() + 1;

        return castingUsers.size();
//        return castingUsers.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isLastPage && position == castingUsers.size()) {
            return TYPE_FOOTER;
        }

        return super.getItemViewType(position);
    }


    public void addItem(CastingUser castingUser) {
        castingUsers.add(castingUser);
        notifyItemInserted(castingUsers.size());
    }

    public void removeItem(int position) {
        castingUsers.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, castingUsers.size());
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView cUserImg;
        private TextView cName, cUserLoc, cDates, cDescription;
        private CardView userAction;

        public ItemViewHolder(View view) {
            super(view);

            userAction = (CardView) view.findViewById(R.id.user_action);
            cUserImg = (ImageView) view.findViewById(R.id.c_user_img);
            cName = (TextView) view.findViewById(R.id.c_name);
            cName.setTypeface(KenbieApplication.S_BOLD);
            cUserLoc = (TextView) view.findViewById(R.id.c_user_loc);
            cUserLoc.setTypeface(KenbieApplication.S_NORMAL);
            cDates = (TextView) view.findViewById(R.id.c_dates);
            cDates.setTypeface(KenbieApplication.S_NORMAL);
            cDescription = (TextView) view.findViewById(R.id.c_description);
            cDescription.setTypeface(KenbieApplication.S_NORMAL);
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

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                try {
                    String text;
                    int lineEndIndex;
                    ViewTreeObserver obs = tv.getViewTreeObserver();
                    obs.removeGlobalOnLayoutListener(this);
                    if (maxLine == 0) {
                        lineEndIndex = tv.getLayout().getLineEnd(0);
                        text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                        lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                        text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    } else {
                        lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                        text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    }
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {
            ssb.setSpan(new MySpannable(false) {
                @Override
                public void onClick(View widget) {
                    /*tv.setLayoutParams(tv.getLayoutParams());
                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                    tv.invalidate();
                    if (viewMore) {
                        makeTextViewResizable(tv, -1, "View Less", false);
                    } else {
                        makeTextViewResizable(tv, 3, "View More", true);
                    }*/
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);
        }
        return ssb;
    }

    private static SpannableStringBuilder addClickablePartTextViewResizable1(final Spanned strSpanned, final TextView tv,
                                                                             final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                    tv.setLayoutParams(tv.getLayoutParams());
                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                    tv.invalidate();
                    if (viewMore) {
                        makeTextViewResizable(tv, -1, "Learn less", false);
                    } else {
                        makeTextViewResizable(tv, 3, "Learn more", true);
                    }

                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }

}
