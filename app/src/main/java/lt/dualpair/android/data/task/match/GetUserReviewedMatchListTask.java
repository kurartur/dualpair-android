package lt.dualpair.android.data.task.match;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import lt.dualpair.android.data.remote.client.match.GetUserMatchListClient;
import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.MatchRepository;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.task.AuthenticatedUserTask;
import rx.Observable;
import rx.Subscriber;

public class GetUserReviewedMatchListTask extends AuthenticatedUserTask<Match> {

    private int start;
    private int count;

    public GetUserReviewedMatchListTask(String authToken, int start, int count) {
        super(authToken);
        this.start = start;
        this.count = count;
    }

    @Override
    protected Observable<Match> run(final Context context) {
        return Observable.create(new Observable.OnSubscribe<Match>() {
            @Override
            public void call(Subscriber<? super Match> subscriber) {
                MatchRepository matchRepository = new MatchRepository(DatabaseHelper.getInstance(context).getWritableDatabase());
                List<Match> matches = new ArrayList<>(); // TODO find matches
                for (Match match : matches) {
                    subscriber.onNext(match);
                }
                if (matches.size() < count) {
                    // TODO ask from service
                    new GetUserMatchListClient(getUserId(context), GetUserMatchListClient.REVIEWED);
                }
                subscriber.onCompleted();
            }
        });
    }

}
