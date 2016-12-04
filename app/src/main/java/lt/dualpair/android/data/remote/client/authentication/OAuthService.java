package lt.dualpair.android.data.remote.client.authentication;

import lt.dualpair.android.data.resource.Token;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

public interface OAuthService {

    @FormUrlEncoded
    @POST("/oauth/token")
    Observable<Token> getToken(
            @Field("grant_type") String grantType,
            @Field("provider") String providerId,
            @Field("access_token") String accessToken,
            @Field("expires_in") Long expiresIn,
            @Field("scope") String scope);

    @FormUrlEncoded
    @POST("/oauth/token")
    Observable<Token> getToken(@Field("refresh_token") String refreshToken, @Field("grant_type") String grantType);

    @POST("/signout")
    Observable<Void> logout();

}
