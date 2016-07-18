package com.artur.dualpair.android.services.user;

import com.artur.dualpair.android.dto.Location;
import com.artur.dualpair.android.dto.SearchParameters;
import com.artur.dualpair.android.dto.Sociotype;
import com.artur.dualpair.android.dto.User;

import java.util.Set;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

public interface UserService {

    @GET("api/user")
    Observable<User> getUser();

    @POST("api/user/sociotypes")
    Observable<Void> setSociotypes(@Body Set<Sociotype> sociotypes);

    @POST("api/user/date-of-birth")
    @FormUrlEncoded
    Observable<Void> setDateOfBirth(@Field("dateOfBirth") String date);

    @POST("api/user/search-parameters")
    Observable<Void> setSearchParameters(@Body SearchParameters searchParameters);


    @POST("api/user/location")
    Observable<Void> setLocation(@Body Location location);

}
