package lt.dualpair.android.ui.match;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.ui.BaseFragment;
import lt.dualpair.android.ui.ScrollSwipeRefreshLayout;

public abstract class MatchListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MatchListFragment";

    private ScrollSwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.mutual_matches) RecyclerView matchesView;
    @Bind(android.R.id.empty) View emptyView;
    @Bind(R.id.no_matches_text) TextView emptyText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.match_list_layout, container, false);
        swipeRefreshLayout = (ScrollSwipeRefreshLayout)view;
        ButterKnife.bind(this, view);
        emptyText.setText(getEmptyViewText());
        swipeRefreshLayout.setView(matchesView);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getPresenter() == null) {
            swipeRefreshLayout.setRefreshing(true);
            createPresenter();
        }
        getPresenter().onTakeView(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getPresenter() != null) {
            getPresenter().onTakeView(null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getPresenter().onSave(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPresenter().onTakeView(null);
        destroyPresenter();
    }

    public void setAdapter(MatchListRecyclerAdapter adapter) {
        matchesView.setAdapter(adapter);
    }

    public void stopRefreshing() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        getPresenter().refresh(getActivity());
    }

    protected abstract String getEmptyViewText();

    protected abstract void createPresenter();

    protected abstract void destroyPresenter();

    protected abstract MatchListPresenter getPresenter();

}
