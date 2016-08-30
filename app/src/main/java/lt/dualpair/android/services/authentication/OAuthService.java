package lt.dualpair.android.services.authentication;

import lt.dualpair.android.resource.Token;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

public interface OAuthService {

    @FormUrlEncoded
    @POST("/oauth/token")
    Observable<Token> getToken(
            @Field("code") String code,
            @Field("redirect_uri") String redirectUri,
            @Field("grant_type") String grantType);

}
