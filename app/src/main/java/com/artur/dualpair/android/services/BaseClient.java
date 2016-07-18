package com.artur.dualpair.android.services;

import com.artur.dualpair.android.TokenProvider;
import com.artur.dualpair.android.rx.RxErrorHandlingCallAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public abstract class BaseClient<K> {

    protected static final String API_BASE_URL = "http://10.0.2.2:8080";

    private Retrofit getRetrofit() {
        OkHttpClient.Builder okHttpClientBuilder =
                new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(authorizationInterceptor());

        Retrofit.Builder retrofitBuilder =
                new Retrofit.Builder().baseUrl(API_BASE_URL)
                        .client(okHttpClientBuilder.build())
                        .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(createGson()));
        return retrofitBuilder.build();
    }

    public Observable<K> observable() {
        return errorHandling(getApiObserable(getRetrofit()));
    }

    protected abstract Observable<K> getApiObserable(Retrofit retrofit);

    private HttpLoggingInterceptor httpLoggingInterceptor(HttpLoggingInterceptor.Level logLevel) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(logLevel);
        return interceptor;
    }

    private Observable<K> errorHandling(Observable<K> observable) {
        return observable;/*.onErrorResumeNext(new Func1<Throwable, Observable<? extends K>>() {
            @Override
            public Observable<? extends K> call(Throwable throwable) {
                if (throwable instanceof HttpException) {
                    HttpException httpException = (HttpException)throwable;
                    return Observable.error(new ServiceException(httpException.response().message()));
                } else {
                    return Observable.error(throwable);
                }
            }
        });*/
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
                String token = TokenProvider.getInstance().getToken();
                if (token == null) {
                    return chain.proceed(chain.request());
                }
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

}
