package lt.dualpair.android.ui.match;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.MatchDataManager;
import lt.dualpair.android.data.resource.Location;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.ui.ImageSwipe;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

// TODO very similar to review view, find solution to re-use stuff or merge into one view
public class MatchActivity extends BaseActivity {

    public static final String TAG = "MatchActivity";
    public static final String MATCH_ID = "matchId";

    private static final int UNMATCH_MENU_ITEM = 1;
    private static final int REPORT_MENU_ITEM = 2;

    private Long matchId;

    private ActionBarViewHolder actionBarViewHolder;

    @Bind(R.id.main_layout) View mainLayout;
    @Bind(R.id.progress_layout) View progressLayout;
    @Bind(R.id.error_layout) View errorLayout;

    @Bind(R.id.photos) ImageSwipe photosView;
    @Bind(R.id.sociotypes) TextView sociotypes;
    @Bind(R.id.description) TextView description;

    @Bind(R.id.error_text) TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.match_layout);


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

        matchId = getIntent().getLongExtra(MATCH_ID, -1);
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

    private void renderMatch(Match match) {
        User opponentUser = match.getOpponent().getUser();
        actionBarViewHolder.name.setText(opponentUser.getName());
        actionBarViewHolder.age.setText(getString(R.string.review_age, opponentUser.getAge()));
        Location location = opponentUser.getFirstLocation();
        if (location != null) {
            actionBarViewHolder.city.setText(getString(R.string.review_city, location.getCity(), match.getDistance() / 1000));
        }
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (Sociotype sociotype : opponentUser.getSociotypes()) {
            sb.append(prefix);
            prefix = ", ";
            String code = sociotype.getCode1();
            int titleId = getResources().getIdentifier(code.toLowerCase() + "_title", "string", getPackageName());
            sb.append(getString(titleId) + " (" + sociotype.getCode1() + ")");
        }
        sociotypes.setText(sb);
        description.setText(opponentUser.getDescription());
        photosView.setPhotos(opponentUser.getPhotos());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, UNMATCH_MENU_ITEM, Menu.NONE, R.string.unmatch);
        menu.add(Menu.NONE, REPORT_MENU_ITEM, Menu.NONE, R.string.report);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) return true;
        switch (item.getItemId()) {
            case UNMATCH_MENU_ITEM:
                // TODO unmatch
                return true;
            case REPORT_MENU_ITEM:
                // TODO report
                return true;
        }
        return false;
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
