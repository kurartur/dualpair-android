package lt.dualpair.android.ui.user;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserForView;
import lt.dualpair.android.data.local.entity.UserLocation;
import lt.dualpair.android.ui.BaseFragment;
import lt.dualpair.android.ui.CustomActionBarActivity;
import lt.dualpair.android.ui.CustomActionBarFragment;
import lt.dualpair.android.ui.UserFriendlyErrorConsumer;
import lt.dualpair.android.utils.ToastUtils;

public class UserFragment extends BaseFragment implements CustomActionBarFragment {

    protected static final String ARG_USER_ID = "user_id";

    private static final int REPORT_MENU_ITEM = 1;
    private static final int UNMATCH_MENU_ITEM = 2;

    protected CompositeDisposable disposable = new CompositeDisposable();

    @Bind(R.id.opponent_user_view)
    OpponentUserView opponentUserView;

    private UserLocation lastOpponentLocation;
    private UserLocation lastPrincipalLocation;

    private String username;
    protected Long userId;
    protected boolean isMatch;

    private UserViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.user_layout, container, false);
        ButterKnife.bind(this, view);

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
        viewModel.getUser(getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(this::render, e -> {
                    ToastUtils.show(getActivity(), e.getMessage());
                });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(Menu.NONE, REPORT_MENU_ITEM, Menu.NONE, R.string.report);
        if (isMatch) {
            menu.add(Menu.NONE, UNMATCH_MENU_ITEM, Menu.NONE, R.string.unmatch);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case REPORT_MENU_ITEM:
                reportUser();
                return true;
            case UNMATCH_MENU_ITEM:
                viewModel.unmatch(userId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action() {
                        @Override
                        public void run() {
                            ToastUtils.show(getActivity(), getString(R.string.unmatched, username));
                            if (getActivity() instanceof OnUnmatchListener) {
                                ((OnUnmatchListener)getActivity()).onUnmatch();
                            }
                        }
                    });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    @Override
    public String getActionBarTitle() {
        return username;
    }

    @Override
    public View getActionBarView() {
        return null;
    }

    protected Long getUserId() {
        return getArguments().getLong(ARG_USER_ID);
    }

    protected void render(UserForView user) {
        User opponentUser = user.getUser();
        username = user.getUser().getName();
        userId = opponentUser.getId();
        lastOpponentLocation = user.getLastLocation();
        opponentUserView.setData(
                opponentUser,
                user.getSociotypes(),
                opponentUser.getDescription(),
                user.getPhotos(),
                opponentUser.getRelationshipStatus(),
                user.getPurposesOfBeing(),
                user.getAccounts()
        );

        opponentUserView.setLocation(lastPrincipalLocation, lastOpponentLocation);

        isMatch = user.getMatch() != null;

        viewModel.getLastStoredLocation().observe(this, userLocation -> {
            lastPrincipalLocation = userLocation;
            opponentUserView.setLocation(userLocation, lastOpponentLocation);
        });

        FragmentActivity activity = getActivity();
        if (activity instanceof CustomActionBarActivity) {
            ((CustomActionBarActivity) activity).requestActionBar(this);
        }
    }

    private void reportUser() {
        new AlertDialog.Builder(getContext())
                .setMessage(getString(R.string.report_user_confirmation, username))
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
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

    public static UserFragment newInstance(Long userId) {
        UserFragment f = new UserFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_USER_ID, userId);
        f.setArguments(bundle);
        return f;
    }

    protected static class SocialButtonsViewHolder {


        public SocialButtonsViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface OnUnmatchListener {
        void onUnmatch();
    }

    public interface OnReportListener {
        void onReport();
    }

}