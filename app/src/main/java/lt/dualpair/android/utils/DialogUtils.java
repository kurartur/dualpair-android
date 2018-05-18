package lt.dualpair.android.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;

public class DialogUtils {

    public static final void showErrorAndExit(final Activity activity, String title, String text) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(activity);
        }
        builder.setTitle(title)
                .setMessage(text)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finishAffinity();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
