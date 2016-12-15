package lt.dualpair.android.ui.main;

import lt.dualpair.android.R;
import lt.dualpair.android.bus.NewMatchEvent;
import lt.dualpair.android.bus.RxBus;
import lt.dualpair.android.ui.match.MatchListFragment;
import lt.dualpair.android.ui.match.MatchListPresenter;
import lt.dualpair.android.ui.match.MatchListRecyclerAdapter;
import rx.Subscription;
import rx.functions.Action1;

public class MutualMatchListFragment extends MatchListFragment {

    private static MutualMatchListPresenter presenter;

    private Subscription newMatchEventSubscription;

    public MutualMatchListFragment() {
        super();
    }

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
        presenter.loadAndPrependMatch(getActivity(), matchId);
    }

    @Override
    protected String getEmptyViewText() {
        return getResources().getString(R.string.you_have_no_matches);
    }

    @Override
    protected void createPresenter() {
        presenter = new MutualMatchListPresenter(getActivity(), new MatchListRecyclerAdapter());
    }

    @Override
    protected void destroyPresenter() {
        presenter = null;
    }

    @Override
    protected MatchListPresenter getPresenter() {
        return presenter;
    }
}
