package lt.dualpair.android.data.remote.client.user;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lt.dualpair.android.data.resource.Location;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.resource.SearchParameters;
import lt.dualpair.android.data.resource.User;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
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

    @PATCH("api/user/{userId}")
    Observable<Void> updateUser(@Path("userId") Long userId, @Body Map<String, Object> data);

    @GET("api/user/{userId}/available-photos")
    Observable<List<Photo>> getAvailablePhotos(@Path("userId") Long userId, @Query("at") String accountType);

    @DELETE("api/user/{userId}/photos/{photoId}")
    Observable<Void> deletePhoto(@Path("userId") Long userId, @Path("photoId") Long photoId);

    @PUT("api/user/{userId}/photos")
    Observable<Photo> addPhoto(@Path("userId") Long userId, @Body Photo photo);

}
