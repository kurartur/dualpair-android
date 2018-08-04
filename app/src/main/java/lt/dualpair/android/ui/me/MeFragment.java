package lt.dualpair.android.ui.me;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.ConnectivityMonitor;
import lt.dualpair.android.R;
import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.ui.AboutActivity;
import lt.dualpair.android.ui.BaseFragment;
import lt.dualpair.android.ui.accounts.EditAccountsActivity;
import lt.dualpair.android.ui.splash.SplashActivity;
import lt.dualpair.android.ui.user.EditPhotosActivity;
import lt.dualpair.android.ui.user.EditUserActivity;
import lt.dualpair.android.ui.user.UserActivity;
import lt.dualpair.android.utils.LabelUtils;
import lt.dualpair.android.utils.ToastUtils;

public class MeFragment extends BaseFragment {

    private MeViewModel viewModel;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Bind(R.id.sociotype) TextView sociotypeView;
    @Bind(R.id.name) TextView nameView;
    @Bind(R.id.photo) ImageView photoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.me_layout, container, false);
        ButterKnife.bind(this, view);
        requestOfflineNotification(view.findViewById(R.id.offline));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(MeViewModel.class);
        subscribeUi();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.me_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           case R.id.about_menu_item:
                startActivity(AboutActivity.createIntent(this.getActivity()));
                break;
            case R.id.logout_menu_item:
                viewModel.logout()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                                MeFragment.this.disposable.clear();
                            }
                        })
                        .subscribe(() -> {
                            Intent newIntent = SplashActivity.createIntent(getActivity());
                            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(newIntent);
                        });
                break;
        }
        return false;
    }

    @SuppressLint("CheckResult")
    private void subscribeUi() {
        disposable.add(viewModel.getSociotype()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sociotype -> {
                    sociotypeView.setText(LabelUtils.getSociotypeAcronym(getContext(), sociotype.getCode()) + " (" + LabelUtils.getSociotype4LetterAcronym(getContext(), sociotype.getCode()) + ")");
                }));
        disposable.add(viewModel.getUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    nameView.setText(user.getName());
                }));
        disposable.add(viewModel.getPhotos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(photos -> {
                    Picasso.with(getActivity())
                            .load(photos.get(0).getSourceLink())
                            .placeholder(R.drawable.person)
                            .error(R.drawable.person)
                            .into(photoView);
                }));
    }

    @OnClick(R.id.about_you_item) void onAboutYouItemClick(View v) {
        startActivity(EditUserActivity.createIntent(getActivity()));
    }

    @OnClick(R.id.photos_item) void onPhotosItemClick(View v) {
        startActivity(EditPhotosActivity.createIntent(getActivity()));
    }

    @OnClick(R.id.sociotype_item) void onSociotypesItemClick(View v) {
        startActivity(MySociotypeActivity.createIntent(getActivity()));
    }

    @OnClick(R.id.accounts_item) void onAccountsItemClick(View v) {
        startActivity(EditAccountsActivity.createIntent(getActivity()));
    }

    @OnClick(R.id.invite_item) void onInviteItemClick(View v) {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String strShareMessage = getString(R.string.recommendation_text) + "\n\n";
            strShareMessage = strShareMessage + "https://play.google.com/store/apps/details?id=" + getActivity().getPackageName();
            i.putExtra(Intent.EXTRA_TEXT, strShareMessage);
            startActivity(Intent.createChooser(i, getString(R.string.share_invite_via)));
        } catch(Exception e) {
            Log.e(getClass().getName(), e.getMessage(), e);
            ToastUtils.show(getContext(), getString(R.string.unexpected_error));
        }
    }

    @OnClick(R.id.view_public_profile) void onViewPublicProfileClick(View v) {
        startActivity(UserActivity.createIntent(getActivity(), AccountUtils.getUserId(getActivity())));
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

    public static MeFragment newInstance() {
        return new MeFragment();
    }

}
