package lt.dualpair.android.data.remote.client.authentication;

import android.util.Base64;

import java.io.IOException;

import io.reactivex.Observable;
import lt.dualpair.android.data.remote.client.ObservableClient;
import lt.dualpair.android.data.resource.Token;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

public class RequestTokenClient extends ObservableClient<Token> {

    private static final String SOCIAL_GRANT_TYPE = "social";
    private static final String REFRESH_TOKEN_GRANT_TYPE = "refresh_token";

    private String clientId;
    private String clientSecret;

    private String grantType;

    private String refreshToken;

    private String providerId;
    private String accessToken;
    private Long expiresIn;
    private String scope;

    public RequestTokenClient(String providerId, String accessToken, Long expiresIn, String scope, String clientId, String clientSecret) {
        this.providerId = providerId;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.scope = scope;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.grantType = SOCIAL_GRANT_TYPE;
    }

    public RequestTokenClient(String refreshToken, String clientId, String clientSecret) {
        this.refreshToken = refreshToken;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.grantType = REFRESH_TOKEN_GRANT_TYPE;
    }

    @Override
    protected Observable<Token> getApiObserable(Retrofit retrofit) {
        OAuthService oAuthService = retrofit.create(OAuthService.class);
        if (SOCIAL_GRANT_TYPE.equals(grantType)) {
            return oAuthService.getToken(grantType, providerId, accessToken, expiresIn, scope);
        } else if (REFRESH_TOKEN_GRANT_TYPE.equals(grantType)) {
            return oAuthService.getToken(refreshToken, grantType);
        }
        throw new UnsupportedOperationException("Unknown grant type");
    }

    @Override
    protected Interceptor authorizationInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String credentials = clientId + ":" + clientSecret;
                String basicAuth = "Basic " + new String(Base64.encode(credentials.getBytes(), Base64.DEFAULT));
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Accept", "application/json")
                        .header("Authorization", basicAuth.trim())
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };
    }
}
