package lt.dualpair.android.data.task.match;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lt.dualpair.android.data.remote.client.match.SetResponseClient;
import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.MatchRepository;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.MatchParty;
import lt.dualpair.android.data.resource.Response;
import lt.dualpair.android.data.task.AuthenticatedUserTask;
import rx.Observable;
import rx.Subscriber;

public class SetResponseTask extends AuthenticatedUserTask<Match> {

    private Long matchId;
    private Response response;

    public SetResponseTask(String authToken, Long matchId, Response response) {
        super(authToken);
        this.matchId = matchId;
        this.response = response;

    }

    @Override
    protected Observable<Match> run(final Context context) {
        return Observable.create(new Observable.OnSubscribe<Match>() {
            @Override
            public void call(Subscriber<? super Match> subscriber) {
                SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
                MatchRepository matchRepository = new MatchRepository(db);
                Match match = matchRepository.findOne(matchId, getUserId(context));
                MatchParty matchParty = match.getUser();
                new SetResponseClient(matchParty.getId(), response).observable().toBlocking().first();
                match.getUser().setResponse(response);
                matchRepository.setResponse(matchParty.getId(), response);
                subscriber.onNext(match);
                subscriber.onCompleted();
            }
        });
    }

}
