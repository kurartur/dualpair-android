package lt.dualpair.android.utils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import lt.dualpair.android.R;

public class DrawableUtils {

    public static Drawable getActionBarIcon(Context context, int drawableId) {
        Drawable drawable = context.getResources().getDrawable(drawableId);
        drawable.setColorFilter(context.getResources().getColor(R.color.actionBarIcons), PorterDuff.Mode.SRC_ATOP);
        return drawable;
    }

    public static void setAccentColorFilter(Context context, Drawable drawable) {
        drawable.setColorFilter(context.getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
    }

    public static void setActionBarIconColorFilter(Context context, Drawable drawable) {
        drawable.setColorFilter(context.getResources().getColor(R.color.actionBarIcons), PorterDuff.Mode.SRC_ATOP);
    }
}
