package lt.dualpair.android.ui.match;


import android.content.Context;

import lt.dualpair.android.R;
import lt.dualpair.android.data.ResourceCollectionLoader;
import lt.dualpair.android.data.manager.MatchDataManager;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.ResourceCollection;
import rx.Observable;

public class ReviewHistoryMatchListFragment extends MatchListFragment {

    @Override
    protected ResourceCollectionLoader<Match> createLoader() {
        return new ResourceCollectionLoader<Match>(getActivity()) {
            @Override
            protected Observable<ResourceCollection<Match>> resourceObservable(Context context, String url) {
                return new MatchDataManager(context).reviewedMatchList(url);
            }
        };
    }

    @Override
    protected String getEmptyViewText() {
        return getResources().getString(R.string.empty_history);
    }
}
