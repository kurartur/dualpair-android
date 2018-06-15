package lt.dualpair.android.data.remote.client.user;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Completable;
import io.reactivex.Observable;
import lt.dualpair.android.data.remote.resource.Location;
import lt.dualpair.android.data.remote.resource.Photo;
import lt.dualpair.android.data.remote.resource.SearchParameters;
import lt.dualpair.android.data.remote.resource.User;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {

    @GET("api/me")
    Observable<User> getUser();

    @PUT("api/user/{userId}/sociotypes")
    Completable setSociotypes(@Path("userId") Long userId, @Body Set<String> codes);

    @PUT("api/user/{userId}/date-of-birth")
    @FormUrlEncoded
    Completable setDateOfBirth(@Path("userId") Long userId, @Field("dateOfBirth") String date);

    @PUT("api/user/{userId}/search-parameters")
    Completable setSearchParameters(@Path("userId") Long userId, @Body SearchParameters searchParameters);

    @PUT("api/user/{userId}/locations")
    Completable setLocation(@Path("userId") Long userId, @Body Location location);

    @GET("api/user/{userId}/search-parameters")
    Observable<SearchParameters> getSearchParameters(@Path("userId") Long userId);

    @PATCH("api/user/{userId}")
    Completable updateUser(@Path("userId") Long userId, @Body Map<String, Object> data);

    @GET("api/user/{userId}/available-photos")
    Observable<List<Photo>> getAvailablePhotos(@Path("userId") Long userId, @Query("at") String accountType);

    @PUT("api/user/{userId}/photos")
    Observable<Photo> addPhoto(@Path("userId") Long userId, @Body Photo photo);

    @POST("api/connect")
    @FormUrlEncoded
    Completable connect(@Field("provider") String provider,
                             @Field("accessToken") String accessToken,
                             @Field("expiresIn") Long expiresIn,
                             @Field("scope") String scope);

    @POST("api/user/{userId}/photos")
    Completable setPhotos(@Path("userId") Long userId, @Body List<Photo> photos);

    @POST("api/report")
    Completable reportUser(@Body Map<String, Object> data);

    @PUT("/api/party/{matchPartyId}/response")
    Completable setResponse(@Path("matchPartyId") Long matchPartyId, @Body String response);
}
