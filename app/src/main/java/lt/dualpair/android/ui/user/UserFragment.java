package lt.dualpair.android.ui.user;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.ConnectivityMonitor;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserForView;
import lt.dualpair.android.data.local.entity.UserLocation;
import lt.dualpair.android.data.remote.client.ServiceException;
import lt.dualpair.android.ui.BaseFragment;
import lt.dualpair.android.ui.UserFriendlyErrorConsumer;
import lt.dualpair.android.ui.VisibilitySwitcher;
import lt.dualpair.android.utils.ToastUtils;

public class UserFragment extends BaseFragment {

    protected static final String ARG_USER_ID = "user_id";

    protected CompositeDisposable disposable = new CompositeDisposable();

    OpponentUserViewHolder opponentUserViewHolder;

    private UserLocation lastOpponentLocation;
    private UserLocation lastPrincipalLocation;

    private String username;
    protected Long userId;
    protected boolean isMatch;

    private UserViewModel viewModel;

    private VisibilitySwitcher visibilitySwitcher;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.user_layout, container, false);
        ButterKnife.bind(this, view);
        opponentUserViewHolder = new OpponentUserViewHolder(getContext(), view);
        requestOfflineNotification(view.findViewById(R.id.offline));
        visibilitySwitcher = new VisibilitySwitcher(view, R.id.loading, R.id.unexpected_error, R.id.no_connection, R.id.main_layout);
        visibilitySwitcher.switchTo(R.id.loading);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity(), new UserViewModel.Factory(getActivity().getApplication())).get(UserViewModel.class);
        subscribeUi();
    }

    @SuppressLint("CheckResult")
    private void subscribeUi() {
        if (!isNetworkAvailable()) {
            visibilitySwitcher.switchTo(R.id.no_connection);
        }
        viewModel.getUser(getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread(), true)
                .compose(bindToLifecycle())
                .subscribe(this::render, e -> {
                    Log.e(UserFragment.class.getName(), e.getMessage(), e);
                    if (userId == null) {
                        if (e instanceof ServiceException) {
                            if (((ServiceException) e).getKind() == ServiceException.Kind.NETWORK) {
                                visibilitySwitcher.switchTo(R.id.no_connection);
                                return;
                            }
                        }
                        visibilitySwitcher.switchTo(R.id.unexpected_error);
                    }
                });
    }

    @OnClick(R.id.more_menu) void onMoreMenuClick(View view) {
        final PopupMenu popup = new PopupMenu(getContext(), view);
        popup.getMenuInflater().inflate(R.menu.user_menu, popup.getMenu());

        if (!isMatch) {
            popup.getMenu().findItem(R.id.unmatch_menu_item).setVisible(false);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                switch (item.getItemId()) {
                    case R.id.report_menu_item:
                        reportUser();
                        return true;
                    case R.id.unmatch_menu_item:
                        unmatchUser();
                        return true;
                }
                return false;
            }
        });
        popup.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    protected Long getUserId() {
        return getArguments().getLong(ARG_USER_ID);
    }

    protected void render(UserForView user) {
        User opponentUser = user.getUser();
        username = user.getUser().getName();
        userId = opponentUser.getId();
        lastOpponentLocation = user.getLastLocation();
        opponentUserViewHolder.setData(
                opponentUser,
                user.getSociotypes(),
                opponentUser.getDescription(),
                user.getPhotos(),
                opponentUser.getRelationshipStatus(),
                user.getPurposesOfBeing(),
                user.getAccounts()
        );

        opponentUserViewHolder.setLocation(lastPrincipalLocation, lastOpponentLocation);

        isMatch = user.getMatch() != null;

        viewModel.getLastStoredLocation().observe(this, userLocation -> {
            lastPrincipalLocation = userLocation;
            opponentUserViewHolder.setLocation(userLocation, lastOpponentLocation);
        });

        visibilitySwitcher.switchTo(R.id.main_layout);
    }

    private void reportUser() {
        if (!isNetworkAvailable()) {
            ToastUtils.show(getContext(), getString(R.string.cant_report_offline));
            return;
        }
        new AlertDialog.Builder(getContext())
                .setMessage(getString(R.string.report_user_confirmation, username))
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                }).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                    @SuppressLint("CheckResult")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.report(userId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .compose(bindToLifecycle())
                            .subscribe(new Action() {
                                @Override
                                public void run() {
                                    if (isMatch) {
                                        ToastUtils.show(getActivity(), getString(R.string.user_reported_and_unmatched, username));
                                    } else {
                                        ToastUtils.show(getActivity(), getString(R.string.user_reported, username));
                                    }
                                    if (getActivity() instanceof OnReportListener) {
                                        ((OnReportListener)getActivity()).onReport();
                                    }
                                }
                            }, new UserFriendlyErrorConsumer(UserFragment.this));
                    }
        }).show();
    }

    @SuppressLint("CheckResult")
    private void unmatchUser() {
        if (!isNetworkAvailable()) {
            ToastUtils.show(getContext(), getString(R.string.cant_unmatch_offline));
            return;
        }
        viewModel.unmatch(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .compose(bindToLifecycle())
                .subscribe(new Action() {
                    @Override
                    public void run() {
                        ToastUtils.show(getActivity(), getString(R.string.unmatched, username));
                        if (getActivity() instanceof OnUnmatchListener) {
                            ((OnUnmatchListener)getActivity()).onUnmatch();
                        }
                    }
                }, new UserFriendlyErrorConsumer(UserFragment.this));
    }

    @SuppressLint("CheckResult")
    protected void requestOfflineNotification(final View offlineNotificationView) {
        ConnectivityMonitor.getInstance().getConnectivityInfo()
                .compose(bindToLifecycle())
                .subscribe(new Consumer<ConnectivityMonitor.ConnectivityInfo>() {
                    @Override
                    public void accept(ConnectivityMonitor.ConnectivityInfo connectivityInfo) throws Exception {
                        if (offlineNotificationView != null) {
                            offlineNotificationView.setVisibility(connectivityInfo.isNetworkAvailable() ? View.GONE : View.VISIBLE);
                        }
                    }
                });
    }

    private boolean isNetworkAvailable() {
        return ConnectivityMonitor.getInstance().getConnectivityInfo().blockingFirst().isNetworkAvailable();
    }

    public static UserFragment newInstance(Long userId) {
        UserFragment f = new UserFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_USER_ID, userId);
        f.setArguments(bundle);
        return f;
    }

    public interface OnUnmatchListener {
        void onUnmatch();
    }

    public interface OnReportListener {
        void onReport();
    }

}
