package lt.dualpair.android.ui;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

public class ScrollSwipeRefreshLayout extends SwipeRefreshLayout {

    private View view;

    public ScrollSwipeRefreshLayout(Context context) {
        super(context);
    }

    public ScrollSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canChildScrollUp() {
        if (view == null) {
            return true;
        }
        return view.canScrollVertically(-1);
    }

    public void setView(View view) {
        this.view = view;
    }
}
