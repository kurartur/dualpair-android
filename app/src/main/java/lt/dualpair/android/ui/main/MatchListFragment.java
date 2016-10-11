package lt.dualpair.android.ui.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import lt.dualpair.android.R;
import lt.dualpair.android.bus.NewMatchEvent;
import lt.dualpair.android.bus.RxBus;
import lt.dualpair.android.data.DefaultErrorHandlingSubscriber;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.ResourceCollectionLoader;
import lt.dualpair.android.data.manager.MatchDataManager;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.ResourceCollection;
import lt.dualpair.android.data.task.match.GetMutualMatchTask;
import lt.dualpair.android.ui.BaseFragment;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MatchListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MatchListFragment";

    private Activity activity;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GridView gridView;
    private MatchListAdapter matchListAdapter;

    private List<Match> matches = new ArrayList<>();
    private ResourceCollectionLoader<Match> loader;
    private Subscription subscription;

    private Subscription newMatchEventSubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loader = new ResourceCollectionLoader<Match>(this.getActivity()) {
            @Override
            protected Observable<ResourceCollection<Match>> resourceObservable(Context context, String url) {
                return new MatchDataManager(context).mutualMatchList(url);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.match_list_layout, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout)view;
        gridView = (GridView)swipeRefreshLayout.findViewById(R.id.mutual_matches_grid);
        gridView.setEmptyView(view.findViewById(android.R.id.empty));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = this.getActivity();
        matchListAdapter = new MatchListAdapter(matches, this.getActivity());
        gridView.setAdapter(matchListAdapter);

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (visibleItemCount == totalItemCount) {
                    loader.loadNext();
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
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
        initializeList();
    }

    @Override
    public void onPause() {
        super.onPause();
        newMatchEventSubscription.unsubscribe();
    }

    private void initializeList() {
        swipeRefreshLayout.setRefreshing(true);

        subscription = loader.load().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .compose(this.<List<Match>>bindToLifecycle())
                .subscribe(new EmptySubscriber<List<Match>>() {
                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Unable to load match list", e);
                    }

                    @Override
                    public void onNext(List<Match> m) {
                        if (!m.isEmpty()) {
                            matches.addAll(m);
                            matchListAdapter.notifyDataSetChanged();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void loadAndPrependMatch(Long matchId) {
        new GetMutualMatchTask(activity, matchId).execute(new DefaultErrorHandlingSubscriber<Match>(activity) {
            @Override
            public void onNext(Match match) {
                ArrayList<Match> tmpMatches = new ArrayList<>(matches);
                matches.clear();
                matches.add(match);
                matches.addAll(tmpMatches);
                matchListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onRefresh() {
        subscription.unsubscribe();
        initializeList();
    }
}
