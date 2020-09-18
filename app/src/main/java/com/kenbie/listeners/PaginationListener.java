package com.kenbie.listeners;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class PaginationListener extends RecyclerView.OnScrollListener {

    public static final int PAGE_START = 1;

    @NonNull
    private GridLayoutManager layoutManager;

    /**
     * Set scrolling threshold here (for now i'm assuming 10 item in one page)
     */
    private static final int PAGE_SIZE = 10;
    private static int type = 0;

    /**
     * Supporting only LinearLayoutManager for now.
     */
    public PaginationListener(@NonNull GridLayoutManager layoutManager, int type) {
        this.layoutManager = layoutManager;
        this.type = type;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= PAGE_SIZE && type != 2) {
                loadMoreItems();
            } else {
                if ((visibleItemCount + firstVisibleItemPosition) == totalItemCount)
                    noTextVisible(true);
                else
                    noTextVisible(false);
            }
        } else if (isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) == totalItemCount)
                noTextVisible(true);
            else
                noTextVisible(false);
        }
    }

    protected abstract void loadMoreItems();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();

    public abstract void noTextVisible(boolean b);
}