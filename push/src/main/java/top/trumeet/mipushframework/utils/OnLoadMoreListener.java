package top.trumeet.mipushframework.utils;

import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class OnLoadMoreListener extends RecyclerView.OnScrollListener {

    private LinearLayoutManager layoutManager;
    private int itemCount;
    private int lastPosition;

    public abstract void onLoadMore();

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            itemCount = layoutManager.getItemCount();
            lastPosition = layoutManager.findLastCompletelyVisibleItemPosition();
        } else {
            Log.e("OnLoadMoreListener", "The OnLoadMoreListener only support LinearLayoutManager");
            return;
        }

        if (lastPosition > itemCount - 3) {
            this.onLoadMore();
        }
    }
}