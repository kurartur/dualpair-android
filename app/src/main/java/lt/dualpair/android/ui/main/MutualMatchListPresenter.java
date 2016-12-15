package lt.dualpair.android.ui.main;

import android.content.Context;

import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.MatchDataManager;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.ui.match.MatchListPresenter;
import lt.dualpair.android.ui.match.MatchListRecyclerAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MutualMatchListPresenter extends MatchListPresenter {

    public MutualMatchListPresenter(Context context, MatchListRecyclerAdapter adpter) {
        super(context, adpter);
    }

    @Override
    protected Observable<Match> observable(Context ctx, int start, int count) {
        return new MatchDataManager(ctx).mutualMatches(start, count);
    }

    public void loadAndPrependMatch(Context context, Long matchId) {
        new MatchDataManager(context).match(matchId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptySubscriber<Match>() {
                    @Override
                    public void onNext(Match match) {
                        adapter.prepend(match);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
