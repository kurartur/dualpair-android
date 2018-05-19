package lt.dualpair.android.data.task.match;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.remote.client.ServiceException;
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

    public SetResponseTask(Long matchId, Response response) {
        this.matchId = matchId;
        this.response = response;

    }

    @Override
    protected Observable<Match> run(final Context context) {
        return Observable.create(new Observable.OnSubscribe<Match>() {
            @Override
            public void call(final Subscriber<? super Match> subscriber) {
                SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
                final MatchRepository matchRepository = new MatchRepository(db);
                final Match match = matchRepository.findOne(matchId, getUserId(context));
                // TODO handle match null case
                final MatchParty matchParty = match.getUser();
                new SetResponseClient(matchParty.getId(), response).observable().subscribe(new EmptySubscriber<Void>() {

                    @Override
                    public void onCompleted() {
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof ServiceException) {
                            ServiceException se = (ServiceException) e;
                            if (se.getResponse().code() == 404) {
                                matchRepository.delete(match);
                                subscriber.onError(e);
                            }
                        }
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        match.getUser().setResponse(response);
                        matchRepository.setResponse(matchParty.getId(), response);
                        subscriber.onNext(match);
                    }
                });
            }
        });
    }

}
