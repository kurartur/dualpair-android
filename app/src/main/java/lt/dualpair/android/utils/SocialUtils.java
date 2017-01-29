package lt.dualpair.android.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import lt.dualpair.android.data.resource.UserAccount;

public class SocialUtils {

    private static final String TAG = "SocialUtils";

    private static final String FACEBOOK_DOMAIN = "https://www.facebook.com";
    private static final String VKONTAKTE_DOMAIN = "https://vk.com";

    public static void openFacebookUser(Context context, String id) {
        String url = FACEBOOK_DOMAIN + "/" + id;
        Uri uri = Uri.parse(url);
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                // http://stackoverflow.com/a/24547437/1048340
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    public static void openVKontakteUser(Context context, String id) {
        String url = VKONTAKTE_DOMAIN + "/id" + id;
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo("com.vkontakte.android", 0);
            if (applicationInfo.enabled) {
                intent.setPackage("com.vkontakte.android");
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        context.startActivity(intent);
    }

    public static void openUserAccount(Context context, UserAccount userAccount) {
        switch (userAccount.getAccountType()) {
            case FB:
                openFacebookUser(context, userAccount.getAccountId());
                break;
            case VK:
                openVKontakteUser(context, userAccount.getAccountId());
                break;
            default:
                Log.e(TAG, "Unrecognized account type");
        }
    }
}
