package lt.dualpair.android.ui.main;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Display;
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomHeightRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Display display = getDisplay();
        Point size = new Point();
        display.getSize(size);
        this.setMinimumHeight(size.y / 2);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
