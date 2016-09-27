package lt.dualpair.android.data.remote.services.match;

import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.ResourceCollection;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface MatchService {

    @GET("/api/match/next")
    Observable<Match> getNext(@Query("mia") Integer minAge,
                              @Query("maa") Integer maxAge,
                              @Query("sf") String searchFemale,
                              @Query("sm") String searchMale);

    @GET("/api/user/{userId}/mutual-matches?sort=dateCreated&dateCreated.dir=desc")
    Observable<ResourceCollection<Match>> getUserMutualMatches(@Path("userId") Long userId, @Query("timestamp") Long timestamp);

    @GET
    Observable<ResourceCollection<Match>> getUserMutualMatches(@Url String url);

    @GET("/api/user/{userId}/mutual-matches/{matchId}")
    Observable<Match> getUserMutualMatch(@Path("userId") Long userId, @Path("matchId") Long matchId);

    @PUT("/api/party/{matchPartyId}/response")
    Observable<Void> setResponse(@Path("matchPartyId") Long matchPartyId, @Body String response);

}
