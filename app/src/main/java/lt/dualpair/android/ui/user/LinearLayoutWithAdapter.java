package lt.dualpair.android.ui.user;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class LinearLayoutWithAdapter extends LinearLayout {

    private BaseAdapter adapter;

    public LinearLayoutWithAdapter(Context context) {
        super(context);
    }

    public LinearLayoutWithAdapter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayoutWithAdapter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LinearLayoutWithAdapter(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                repopulateViews();
            }

            @Override
            public void onInvalidated() {
                repopulateViews();
            }
        });
    }

    public BaseAdapter getAdapter() {
        return adapter;
    }

    private void repopulateViews() {
        removeAllViews();
        for(int i=0; i<adapter.getCount(); i++) {
            addView(adapter.getView(i, null, this));
        }
    }


}
