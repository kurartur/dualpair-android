package com.artur.dualpair.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class TokenProvider {

    private static TokenProvider tokenProvider;

    private static final String ACCESS_TOKEN = TokenProvider.class.getSimpleName() + ".ACCESS_TOKEN";
    private static final String TOKEN_TYPE = TokenProvider.class.getSimpleName() + ".TOKEN_TYPE";
    private static final String REFRESH_TOKEN = TokenProvider.class.getSimpleName() + ".REFRESH_TOKEN";

    private Context context;

    private TokenProvider(Context context) {
        this.context = context;
    }

    public static void initialize(Context ctx) {
        tokenProvider = new TokenProvider(ctx);
    }

    public static TokenProvider getInstance() {
        if (tokenProvider == null) {
            throw new IllegalStateException(TokenProvider.class.getSimpleName() + " not initialized!");
        }
        return tokenProvider;
    }

    public String getToken() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(ACCESS_TOKEN, null);
    }

    public void storeToken(String token) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ACCESS_TOKEN, token);
        editor.apply();
    }

}
