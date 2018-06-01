package lt.dualpair.android.ui.match;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.data.local.entity.UserForView;
import lt.dualpair.android.data.local.entity.UserLocation;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.ui.user.OpponentUserView;
import lt.dualpair.android.ui.user.SocialButtonsRecyclerAdapter;
import lt.dualpair.android.ui.user.UserViewActionBarViewHolder;
import lt.dualpair.android.utils.SocialUtils;
import lt.dualpair.android.utils.ToastUtils;

public class UserActivity extends BaseActivity {

    private static final String TAG = UserActivity.class.getName();
    private static final String REFERENCE = "reference";

    private static final int REPORT_MENU_ITEM = 1;
    private static final int UNMATCH_MENU_ITEM = 2;
    private static final int UNMATCH_RESULT_CODE = 10;

    private UserViewActionBarViewHolder actionBarViewHolder;

    @Bind(R.id.main_layout)     View mainLayout;
    @Bind(R.id.progress_layout) View progressLayout;
    @Bind(R.id.error_layout)    View errorLayout;

    @Bind(R.id.opponent_user_view) OpponentUserView opponentUserView;
    private SocialButtonsViewHolder socialButtonsViewHolder;

    @Bind(R.id.error_text) TextView errorText;

    private MenuItem unmatchMenuItem;

    private UserViewModel viewModel;

    private String username;
    private boolean isMatched;

    private UserLocation lastPrincipalLocation;
    private UserLocation lastOpponentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.match_layout);
        ButterKnife.bind(this);

        showProgress();

        Long reference = getIntent().getLongExtra(REFERENCE, -1);
        if (reference == -1) {
            throw new RuntimeException("Reference not provided");
        }

        actionBarViewHolder = new UserViewActionBarViewHolder(getLayoutInflater().inflate(R.layout.match_action_bar_layout, null), this);
        socialButtonsViewHolder = new SocialButtonsViewHolder(opponentUserView.setPhotoOverlay(R.layout.match_social_buttons));

        setupActionBar();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setCustomView(actionBarViewHolder.actionBarView);
        }

        viewModel = ViewModelProviders.of(this, new UserViewModel.Factory(getApplication(), reference)).get(UserViewModel.class);
        subscribeUi();
    }



    private void subscribeUi() {
        viewModel.getUser().observe(this, new Observer<UserForView>() {
            @Override
            public void onChanged(@Nullable UserForView userForView) {
                render(userForView);
                showMain();
            }
        });
        viewModel.getLastStoredLocation().observe(this, new Observer<UserLocation>() {
            @Override
            public void onChanged(@Nullable UserLocation userLocation) {
                lastPrincipalLocation = userLocation;
                actionBarViewHolder.setLocation(userLocation, lastOpponentLocation);
            }
        });
    }

    protected void render(UserForView user) {
        User opponentUser = user.getUser();
        username = user.getUser().getName();
        lastOpponentLocation = user.getLastLocation();
        actionBarViewHolder.setUserData(opponentUser);
        actionBarViewHolder.setLocation(lastPrincipalLocation, lastOpponentLocation);
        opponentUserView.setData(
                user.getSociotypes(),
                opponentUser.getDescription(),
                user.getPhotos(),
                opponentUser.getRelationshipStatus(),
                user.getPurposesOfBeing()
        );

        if (user.isMatched()) {
            isMatched = true;
            socialButtonsViewHolder.buttons.setAdapter(new SocialButtonsRecyclerAdapter(user.getAccounts(), new SocialButtonsRecyclerAdapter.OnButtonClick() {
                @Override
                public void onClick(UserAccount userAccount) {
                    SocialUtils.openUserAccount(UserActivity.this, userAccount);
                }
            }));
        } else {
            isMatched = false;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (isMatched) {
            unmatchMenuItem = menu.add(Menu.NONE, UNMATCH_MENU_ITEM, Menu.NONE, R.string.unmatch);
        }
        menu.add(Menu.NONE, REPORT_MENU_ITEM, Menu.NONE, R.string.report);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) return true;
        switch (item.getItemId()) {
            case UNMATCH_MENU_ITEM:
                viewModel.unmatch()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Action() {
                            @Override
                            public void run() {
                                setResult(UNMATCH_RESULT_CODE);
                                finish();
                            }
                        });
                return true;
            case REPORT_MENU_ITEM:
                reportUser();
                return true;
        }
        return false;
    }

    private void reportUser() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.report_user_confirmation, username))
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.report()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {
                                    ToastUtils.show(UserActivity.this, getString(R.string.user_reported, username));
                                }
                            });
                    }
                }).show();
    }

    private void showMain() {
        mainLayout.setVisibility(View.VISIBLE);
        progressLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
    }

    private void showProgress() {
        mainLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
    }

    private void showError(String errorText) {
        mainLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
        this.errorText.setText(errorText);
    }

    public static Intent createIntent(Context ctx, Long reference) {
        Intent intent = new Intent(ctx, UserActivity.class);
        intent.putExtra(REFERENCE, reference);
        return intent;
    }

    protected static class SocialButtonsViewHolder {

        @Bind(R.id.buttons)
        RecyclerView buttons;

        public SocialButtonsViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
