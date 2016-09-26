package lt.dualpair.android.data.remote.services.authentication;

import android.util.Base64;

import java.io.IOException;

import lt.dualpair.android.data.remote.services.BaseClient;
import lt.dualpair.android.data.resource.Token;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import rx.Observable;

public class RequestTokenClient extends BaseClient<Token> {

    private String code;
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String grantType;

    public RequestTokenClient(String code, String clientId, String clientSecret, String redirectUri, String grantType) {
        this.code = code;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.grantType = grantType;
    }

    @Override
    protected Observable<Token> getApiObserable(Retrofit retrofit) {
        return retrofit.create(OAuthService.class).getToken(code, redirectUri, grantType);
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
