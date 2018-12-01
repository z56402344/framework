package z.adapter;

import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by davinci42 on 12/1/16.
 * 配合 MultiTypeAdapter，监听 ListView 滑动，Factory 中处理
 */

public class ScrollMngr extends ArrayList<AbsListView.OnScrollListener> implements AbsListView.OnScrollListener {


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        for (AbsListView.OnScrollListener listener : ScrollMngr.this) {
            if (listener != null) {
                listener.onScrollStateChanged(view, scrollState);
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        for (AbsListView.OnScrollListener listener : ScrollMngr.this) {
            if (listener != null) {
                listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }
    }
}
