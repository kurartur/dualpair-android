package lt.dualpair.android.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    public static void show(final Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

}
