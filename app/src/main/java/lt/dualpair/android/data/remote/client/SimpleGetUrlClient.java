package lt.dualpair.android.data.remote.client;


import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

public class SimpleGetUrlClient<T> extends BaseClient<T> {

    private String url;

    public SimpleGetUrlClient(String url) {
        this.url = url;
    }

    @Override
    protected Observable<T> getApiObserable(Retrofit retrofit) {
        return retrofit.create(SimpleUrlService.class).get(url);
    }

    private interface SimpleUrlService {

        @GET
        <T> Observable<T> get(@Url String url);

    }
}
