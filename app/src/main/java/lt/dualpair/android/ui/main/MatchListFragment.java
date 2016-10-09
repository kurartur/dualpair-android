package lt.dualpair.android.ui.main;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.trello.rxlifecycle.ActivityLifecycleProvider;

import java.util.ArrayList;
import java.util.List;

import lt.dualpair.android.R;
import lt.dualpair.android.bus.NewMatchEvent;
import lt.dualpair.android.bus.RxBus;
import lt.dualpair.android.data.DefaultErrorHandlingSubscriber;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.ResourceCollection;
import lt.dualpair.android.data.task.match.GetMutualMatchTask;
import lt.dualpair.android.data.task.match.GetUserMutualMatchListTask;
import rx.Subscription;
import rx.functions.Action1;

public class MatchListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MatchListFragment";

    private Activity activity;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GridView gridView;
    private MatchListAdapter matchListAdapter;

    private List<Match> matches = new ArrayList<>();
    private ResourceCollection<Match> currentResourceCollection;

    private Subscription newMatchEventSubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        // TODO set on scroll listener that adds more data
        swipeRefreshLayout.setOnRefreshListener(this);
        initializeList();
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

    private void initializeList() {
        swipeRefreshLayout.setRefreshing(true);

        new GetUserMutualMatchListTask(activity).execute(new EmptySubscriber<ResourceCollection<Match>>() {
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Unable to load match list", e);
            }

            @Override
            public void onNext(ResourceCollection<Match> resourceCollection) {
                matches.clear();
                if (!resourceCollection.isEmpty()) {
                    currentResourceCollection = resourceCollection;
                    matches.addAll(resourceCollection.getContent());
                    matchListAdapter.notifyDataSetChanged();
                } else {
                    // TODO show no matches text
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        }, (ActivityLifecycleProvider)activity);
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
        initializeList();
    }
}
