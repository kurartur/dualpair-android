package lt.dualpair.android.data.remote.client.match;

import io.reactivex.Observable;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.ResourceCollection;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MatchService {

    @GET("/api/match/next")
    Observable<Match> getNext(@Query("mia") Integer minAge,
                              @Query("maa") Integer maxAge,
                              @Query("sf") String searchFemale,
                              @Query("sm") String searchMale);

    @GET("/api/user/{userId}/matches?mt=mu")
    Observable<ResourceCollection<Match>> getUserMutualMatches(@Path("userId") Long userId, @Query("timestamp") Long timestamp);

    @GET("/api/user/{userId}/matches/{matchId}")
    Observable<Match> getUserMatch(@Path("userId") Long userId, @Path("matchId") Long matchId);

    @GET("/api/user/{userId}/matches?mt=re")
    Observable<ResourceCollection<Match>> getUserReviewedMatches(@Path("userId") Long userId, @Query("timestamp") Long timestamp);

}
