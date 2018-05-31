package lt.dualpair.android.data.remote.client;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import retrofit2.Retrofit;

public abstract class ObservableClient<K> extends BaseClient {

    public Observable<K> observable() {
        Retrofit retrofit = getRetrofit();
        return getApiObserable(getRetrofit())
                .onErrorResumeNext(new Function<Throwable, Observable<K>>() {
                    @Override
                    public Observable<K> apply(Throwable throwable) throws Exception {
                        ServiceException serviceException = ServiceException.fromThrowable(throwable, retrofit);
                        if (serviceException.isUnauthorized()) {
                            TokenProvider tokenProvider = TokenProvider.getInstance();
                            tokenProvider.requestTokenRefresh().blockingFirst();
                            return observable();
                        }
                        return Observable.error(serviceException);
                    }
                });
    }

    protected abstract Observable<K> getApiObserable(Retrofit retrofit);

}
