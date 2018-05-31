package lt.dualpair.android.data.remote.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import lt.dualpair.android.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public abstract class BaseClient {

    protected Retrofit getRetrofit() {
        OkHttpClient.Builder okHttpClientBuilder =
                new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(authorizationInterceptor())
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS);

        Retrofit.Builder retrofitBuilder =
                new Retrofit.Builder().baseUrl(BuildConfig.SERVER_HOST)
                        .client(okHttpClientBuilder.build())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(createGson()));
        return retrofitBuilder.build();
    }

    private HttpLoggingInterceptor httpLoggingInterceptor(HttpLoggingInterceptor.Level logLevel) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(logLevel);
        return interceptor;
    }

    private Gson createGson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();
    }

    protected Interceptor authorizationInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String token = getAuthToken();

                // Aassume that we always have token
                //if (token == null) {
                //    return chain.proceed(chain.request());
                //}

                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Accept", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };
    }

    protected String getAuthToken() {
        return TokenProvider.getInstance().getAuthToken().blockingFirst();
    }

}
