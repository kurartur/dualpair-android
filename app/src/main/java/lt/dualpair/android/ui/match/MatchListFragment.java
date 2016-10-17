package lt.dualpair.android.ui.match;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.List;

import lt.dualpair.android.R;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.ResourceCollectionLoader;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.ui.BaseFragment;
import lt.dualpair.android.ui.ScrollSwipeRefreshLayout;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class MatchListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MatchListFragment";

    private ScrollSwipeRefreshLayout swipeRefreshLayout;
    private GridView gridView;

    protected MatchListAdapter matchListAdapter;

    private ResourceCollectionLoader<Match> loader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loader = createLoader();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.match_list_layout, container, false);
        swipeRefreshLayout = (ScrollSwipeRefreshLayout)view;
        gridView = (GridView)swipeRefreshLayout.findViewById(R.id.mutual_matches_grid);

        TextView emptyView = (TextView) view.findViewById(android.R.id.empty);
        emptyView.setText(getEmptyViewText());
        gridView.setEmptyView(emptyView);

        swipeRefreshLayout.setView(gridView);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        matchListAdapter = new MatchListAdapter(getActivity());
        gridView.setAdapter(matchListAdapter);

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                /*if (visibleItemCount == totalItemCount) {
                    loader.loadNext();
                }*/
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeList();
    }

    private void initializeList() {
        swipeRefreshLayout.setRefreshing(true);
        matchListAdapter.clear();
        loader.observable().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .compose(this.<List<Match>>bindToLifecycle())
                .subscribe(new MatchListSubscriber());
        loader.load();
    }

    @Override
    public void onRefresh() {
        initializeList();
    }

    protected abstract ResourceCollectionLoader<Match> createLoader();

    protected abstract String getEmptyViewText();

    private class MatchListSubscriber extends EmptySubscriber<List<Match>> {
        @Override
        public void onCompleted() {
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "Unable to load match list", e);
        }

        @Override
        public void onNext(List<Match> m) {
            if (!m.isEmpty()) {
                for (Match match : m) {
                    matchListAdapter.append(match);
                }
                matchListAdapter.notifyDataSetChanged();
            }
        }
    }
}
