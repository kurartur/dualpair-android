package lt.dualpair.android.ui.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.ConnectivityMonitor;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.UserListItem;
import lt.dualpair.android.data.remote.client.ServiceException;
import lt.dualpair.android.ui.BaseFragment;
import lt.dualpair.android.ui.CustomActionBarFragment;
import lt.dualpair.android.ui.ScrollSwipeRefreshLayout;
import lt.dualpair.android.ui.VisibilitySwitcher;
import lt.dualpair.android.ui.user.UserActivity;

public abstract class UserListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        CustomActionBarFragment, UserListRecyclerAdapter.OnItemClickListener {

    private ScrollSwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.user_list) protected RecyclerView listView;
    @Bind(android.R.id.empty) View emptyView;
    @Bind(R.id.no_matches_text) TextView emptyText;

    protected VisibilitySwitcher visibilitySwitcher;

    private CompositeDisposable disposable = new CompositeDisposable();

    boolean isFirstData = false;

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

        swipeRefreshLayout.setView(listView);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setProgressViewOffset(true, 150, 400);

        visibilitySwitcher = new VisibilitySwitcher(view,
                R.id.no_connection, android.R.id.empty, R.id.unexpected_error, R.id.user_list);
        visibilitySwitcher.switchTo(R.id.user_list);
        swipeRefreshLayout.setRefreshing(true);

        requestOfflineNotification(view.findViewById(R.id.offline));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        subscribeUi();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    private void subscribeUi() {
        Disposable d = getItemsFlowable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread(), true)
                .subscribe(items -> {
                    if (items.isEmpty()) {
                        if (!ConnectivityMonitor.getInstance().getConnectivityInfo().blockingFirst().isNetworkAvailable()) {
                            onRefreshed();
                            visibilitySwitcher.switchTo(R.id.no_connection);
                        } else {
                            if (!isFirstData) {
                                onRefreshed();
                                visibilitySwitcher.switchTo(android.R.id.empty);
                            }
                        }
                    } else {
                        onRefreshed();
                        listView.setAdapter(new UserListRecyclerAdapter<>(items, this));
                        visibilitySwitcher.switchTo(R.id.user_list);
                    }
                    isFirstData = true;
                }, throwable -> {
                    onRefreshed();
                    Log.e(getClass().getName(), throwable.getMessage(), throwable);
                });
        disposable.add(d);
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
    public void onClick(UserListItem item) {
        startActivity(UserActivity.createIntent(getContext(), item.getUserId()));
    }

    @SuppressLint("CheckResult")
    protected void requestOfflineNotification(final View offlineNotificationView) {
        ConnectivityMonitor.getInstance().getConnectivityInfo()
                .compose(bindToLifecycle())
                .subscribe(connectivityInfo -> {
                    if (offlineNotificationView != null) {
                        offlineNotificationView.setVisibility(connectivityInfo.isNetworkAvailable() ? View.GONE : View.VISIBLE);
                    }
                });
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        disposable.add(
                getRefreshCompletable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onRefreshed, e -> {
                            onRefreshed();
                            if (listView.getAdapter() == null || listView.getAdapter().getItemCount() == 0) {
                                if (e instanceof ServiceException) {
                                    if (((ServiceException) e).getKind() == ServiceException.Kind.NETWORK) {
                                        return;
                                    }
                                }
                                visibilitySwitcher.switchTo(R.id.unexpected_error);
                            }
                        })
        );
    }

    @Override
    public String getActionBarTitle() {
        return null;
    }

    @Override
    public View getActionBarView() {
        return null;
    }

    protected void onRefreshed() {
        swipeRefreshLayout.setRefreshing(false);
    }


    protected abstract String getEmptyViewText();

    protected abstract Flowable<List<UserListItem>> getItemsFlowable();

    protected abstract Completable getRefreshCompletable();

}
