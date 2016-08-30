package lt.dualpair.android.services.match;

import java.util.List;

import lt.dualpair.android.resource.Match;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface MatchService {

    @GET("/api/match/next")
    Observable<Match> getNext();

    @GET("/api/matches")
    Observable<List<Match>> getList();

    @PUT("/api/match/{matchId}/response")
    Observable<Match> setResponse(@Path("matchId") Long matchId, @Body String response);

}
