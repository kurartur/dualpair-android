package lt.dualpair.android.utils;

import android.os.Build;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;

public class CookieUtils {

    private static final String TAG = "CookieUtils";

    public static void clearCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                @Override
                public void onReceiveValue(Boolean aBoolean) {
                    Log.d(TAG, "Cookie removed: " + aBoolean);
                }
            });
        } else cookieManager.removeAllCookie();
    }

}
