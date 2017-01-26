package lt.dualpair.android.ui.main;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class CustomHeightRelativeLayout extends RelativeLayout {

    public CustomHeightRelativeLayout(Context context) {
        super(context);
    }

    public CustomHeightRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomHeightRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomHeightRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMinimumHeight(parentHeight * 2 / 3);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
