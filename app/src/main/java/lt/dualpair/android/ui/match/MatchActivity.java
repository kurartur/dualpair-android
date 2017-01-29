package lt.dualpair.android.ui.match;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.MatchDataManager;
import lt.dualpair.android.data.manager.UserDataManager;
import lt.dualpair.android.data.remote.client.ServiceException;
import lt.dualpair.android.data.resource.ErrorResponse;
import lt.dualpair.android.data.resource.Location;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.ui.user.OpponentUserView;
import lt.dualpair.android.utils.ToastUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MatchActivity extends BaseActivity {

    public static final String TAG = "MatchActivity";
    public static final String MATCH_ID = "matchId";

    private static final int REPORT_MENU_ITEM = 1;

    private ActionBarViewHolder actionBarViewHolder;

    @Bind(R.id.main_layout)     View mainLayout;
    @Bind(R.id.progress_layout) View progressLayout;
    @Bind(R.id.error_layout)    View errorLayout;

    @Bind(R.id.opponent_user_view) OpponentUserView opponentUserView;

    @Bind(R.id.error_text) TextView errorText;

    protected Match match;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.match_layout);
        ButterKnife.bind(this);

        actionBarViewHolder = new ActionBarViewHolder();
        actionBarViewHolder.actionBarView = getLayoutInflater().inflate(R.layout.match_action_bar_layout, null);
        ButterKnife.bind(actionBarViewHolder, actionBarViewHolder.actionBarView);

        setupActionBar();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setCustomView(actionBarViewHolder.actionBarView);
        }

        Long matchId = getIntent().getLongExtra(MATCH_ID, -1);
        if (matchId == -1) {
            throw new RuntimeException("Match id is empty");
        }
        load(matchId);
    }

    private void load(Long matchId) {
        showProgress();
        new MatchDataManager(this).match(matchId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .compose(this.<Match>bindToLifecycle())
                .subscribe(new EmptySubscriber<Match>() {
                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Unable to load match", e);
                        showError("Unable to load match: " + e.getMessage());
                    }

                    @Override
                    public void onNext(Match match) {
                        renderMatch(match);
                        showMain();
                    }
                });
    }

    protected void renderMatch(Match match) {
        this.match = match;
        User opponentUser = match.getOpponent().getUser();
        actionBarViewHolder.name.setText(opponentUser.getName());
        actionBarViewHolder.age.setText(getString(R.string.review_age, opponentUser.getAge()));
        Location location = opponentUser.getFirstLocation();
        if (location != null) {
            actionBarViewHolder.city.setText(getString(R.string.review_city, location.getCity(), match.getDistance() / 1000));
        }
        opponentUserView.setUser(opponentUser);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, REPORT_MENU_ITEM, Menu.NONE, R.string.report);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) return true;
        switch (item.getItemId()) {
            case REPORT_MENU_ITEM:
                if (match != null) {
                    reportUser(match.getOpponent().getUser());
                }
                return true;
        }
        return false;
    }

    private void reportUser(final User user) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.report_user_confirmation, user.getName()))
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new UserDataManager(MatchActivity.this).reportUser(user.getId())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new EmptySubscriber<Void>() {

                                    @Override
                                    public void onError(Throwable e) {
                                        if (e instanceof ServiceException) {
                                            ServiceException se = (ServiceException)e;
                                            try {
                                                ErrorResponse errorResponse = se.getErrorBodyAs(ErrorResponse.class);
                                                // TODO error message
                                                ToastUtils.show(MatchActivity.this, errorResponse.getMessage());
                                            } catch (IOException ioe) {
                                                throw new RuntimeException("Unable to convert error");
                                            }
                                        } else {
                                            ToastUtils.show(MatchActivity.this, "Unable to report user");
                                        }
                                    }

                                    @Override
                                    public void onNext(Void aVoid) {
                                        ToastUtils.show(MatchActivity.this, getString(R.string.user_reported, user.getName()));
                                    }
                                });
                    }
                }).show();
    }

    public static Intent createIntent(Context ctx, Long matchId) {
        Intent intent = new Intent(ctx, MatchActivity.class);
        intent.putExtra(MATCH_ID, matchId);
        return intent;
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

    protected static class ActionBarViewHolder {

        View actionBarView;

        @Bind(R.id.name) TextView name;
        @Bind(R.id.age)  TextView age;
        @Bind(R.id.city) TextView city;

    }
}
