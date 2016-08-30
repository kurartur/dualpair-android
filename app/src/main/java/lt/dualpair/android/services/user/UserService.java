package lt.dualpair.android.services.user;

import java.util.Set;

import lt.dualpair.android.resource.Location;
import lt.dualpair.android.resource.SearchParameters;
import lt.dualpair.android.resource.User;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface UserService {

    @GET("api/me")
    Observable<User> getUser();

    @PUT("api/user/{userId}/sociotypes")
    Observable<Void> setSociotypes(@Path("userId") Long userId, @Body Set<String> codes);

    @PUT("api/user/{userId}/date-of-birth")
    @FormUrlEncoded
    Observable<Void> setDateOfBirth(@Path("userId") Long userId, @Field("dateOfBirth") String date);

    @PUT("api/user/{userId}/search-parameters")
    Observable<Void> setSearchParameters(@Path("userId") Long userId, @Body SearchParameters searchParameters);

    @PUT("api/user/{userId}/locations")
    Observable<Void> setLocation(@Path("userId") Long userId, @Body Location location);

    @GET("api/user/{userId}/search-parameters")
    Observable<SearchParameters> getSearchParameters(@Path("userId") Long userId);
}
