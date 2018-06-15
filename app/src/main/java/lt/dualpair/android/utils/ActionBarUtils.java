package lt.dualpair.android.utils;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class ActionBarUtils {

    public static void setTitle(Activity activity, String title) {
        if (activity instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity)activity).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(title);
            }
        }
    }

    public static void setHomeButtonEnabled(Activity activity, boolean value) {
        if (activity instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity)activity).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setHomeButtonEnabled(value);
                actionBar.setDisplayHomeAsUpEnabled(value);
            }
        }
    }

}
