package lt.dualpair.android.data.remote.client.authentication;

import android.util.Base64;

import java.io.IOException;

import lt.dualpair.android.data.remote.client.BaseClient;
import lt.dualpair.android.data.resource.Token;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import rx.Observable;

public class RequestTokenClient extends BaseClient<Token> {

    private static final String AUTH_CODE_GRANT_TYPE = "authorization_code";
    private static final String REFRESH_TOKEN_GRANT_TYPE = "refresh_token";

    private String code;
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String grantType;
    private String refreshToken;

    public RequestTokenClient(String code, String clientId, String clientSecret, String redirectUri) {
        this.code = code;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.grantType = AUTH_CODE_GRANT_TYPE;
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
        if (AUTH_CODE_GRANT_TYPE.equals(grantType)) {
            return oAuthService.getToken(code, redirectUri, grantType);
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
