package lt.dualpair.android.ui.main;

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
import lt.dualpair.android.ui.ScrollSwipeRefreshLayout;

public abstract class UserListFragment extends MainTabFragment implements SwipeRefreshLayout.OnRefreshListener {

    private ScrollSwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.mutual_matches) protected RecyclerView matchesView;
    @Bind(android.R.id.empty) View emptyView;
    @Bind(R.id.no_matches_text) TextView emptyText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.user_list_layout, container, false);
        swipeRefreshLayout = (ScrollSwipeRefreshLayout)view;
        ButterKnife.bind(this, view);
        emptyText.setText(getEmptyViewText());
        swipeRefreshLayout.setView(matchesView);
        swipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        emptyText.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);
    }

    public void showEmpty() {
        swipeRefreshLayout.setRefreshing(false);
        emptyText.setVisibility(View.VISIBLE);
    }

    public void showList() {
        swipeRefreshLayout.setRefreshing(false);
        emptyText.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        refresh();
    }

    protected void onRefreshed() {
        swipeRefreshLayout.setRefreshing(false);
    }

    protected abstract void refresh();

    protected abstract String getEmptyViewText();

}
