package lt.dualpair.android.ui.match;

import android.content.Context;

import lt.dualpair.android.data.manager.MatchDataManager;
import lt.dualpair.android.data.resource.Match;
import rx.Observable;

public class HistoryMatchListPresenter extends MatchListPresenter {

    public HistoryMatchListPresenter(Context context, MatchListRecyclerAdapter adpter) {
        super(context, adpter);
    }

    @Override
    protected Observable<Match> observable(Context ctx, int start, int count) {
        return new MatchDataManager(ctx).historyMatches(start, count);
    }


}
