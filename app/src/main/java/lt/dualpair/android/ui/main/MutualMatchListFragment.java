package lt.dualpair.android.ui.main;

import android.content.Context;

import lt.dualpair.android.R;
import lt.dualpair.android.bus.NewMatchEvent;
import lt.dualpair.android.bus.RxBus;
import lt.dualpair.android.data.DefaultErrorHandlingSubscriber;
import lt.dualpair.android.data.ResourceCollectionLoader;
import lt.dualpair.android.data.manager.MatchDataManager;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.ResourceCollection;
import lt.dualpair.android.data.task.match.GetMutualMatchTask;
import lt.dualpair.android.ui.match.MatchListFragment;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

public class MutualMatchListFragment extends MatchListFragment {

    private Subscription newMatchEventSubscription;

    @Override
    public void onResume() {
        super.onResume();
        newMatchEventSubscription = RxBus.getInstance().register(NewMatchEvent.class, new Action1<NewMatchEvent>() {
            @Override
            public void call(NewMatchEvent newMatchEvent) {
                loadAndPrependMatch(newMatchEvent.getMatchId());
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        newMatchEventSubscription.unsubscribe();
    }

    private void loadAndPrependMatch(Long matchId) {
        new GetMutualMatchTask(getActivity(), matchId).execute(new DefaultErrorHandlingSubscriber<Match>(getActivity()) {
            @Override
            public void onNext(Match match) {
                matchListAdapter.prepend(match);
                matchListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected ResourceCollectionLoader<Match> createLoader() {
        return new ResourceCollectionLoader<Match>(this.getActivity()) {
            @Override
            protected Observable<ResourceCollection<Match>> resourceObservable(Context context, String url) {
                return new MatchDataManager(context).mutualMatchList(url);
            }
        };
    }

    @Override
    protected String getEmptyViewText() {
        return getResources().getString(R.string.you_have_no_matches);
    }
}
