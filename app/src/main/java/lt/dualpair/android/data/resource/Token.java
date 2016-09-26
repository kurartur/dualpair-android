package lt.dualpair.android.data.resource;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class Token {

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("refresh_token")
    private String refreshToken;

    public Token(String accessToken, String tokenType, String refreshToken) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        if ( ! Character.isUpperCase(tokenType.charAt(0))) {
            tokenType =
                    Character
                            .toString(tokenType.charAt(0))
                            .toUpperCase() + tokenType.substring(1);
        }
        return tokenType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public boolean isAnyValueEmpty() {
        return TextUtils.isEmpty(accessToken)
                || TextUtils.isEmpty(tokenType)
                || TextUtils.isEmpty(refreshToken);
    }
}
