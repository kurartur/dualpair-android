package com.artur.dualpair.android.utils;

import android.app.Activity;
import android.widget.Toast;

public class ToastUtils {

    public static void show(final Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

}
