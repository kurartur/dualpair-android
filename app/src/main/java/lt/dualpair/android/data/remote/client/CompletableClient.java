package lt.dualpair.android.data.remote.client;

import io.reactivex.Completable;
import io.reactivex.functions.Function;
import retrofit2.Retrofit;

public abstract class CompletableClient extends BaseClient {

    public Completable completable() {
        Retrofit retrofit = getRetrofit();
        return getApiCompletable(retrofit)
                .onErrorResumeNext(new Function<Throwable, Completable>() {
                    @Override
                    public Completable apply(Throwable throwable) throws Exception {
                        ServiceException serviceException = ServiceException.fromThrowable(throwable, retrofit);
                        if (serviceException.isUnauthorized()) {
                            TokenProvider tokenProvider = TokenProvider.getInstance();
                            tokenProvider.requestTokenRefresh().blockingFirst();
                            return completable();
                        }
                        return Completable.error(serviceException);
                    }
                });
    }

    protected abstract Completable getApiCompletable(Retrofit retrofit);
}
