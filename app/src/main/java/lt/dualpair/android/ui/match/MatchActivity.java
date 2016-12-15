package lt.dualpair.android.ui.match;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import lt.dualpair.android.R;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.MatchDataManager;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.ui.BaseActivity;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MatchActivity extends BaseActivity {

    public static final String TAG = "MatchActivity";
    public static final String MATCH_ID = "matchId";

    private Long matchId;

    private OpponentUserView opponentUserView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_layout);
        setupActionBar();
        matchId = getIntent().getLongExtra(MATCH_ID, -1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (opponentUserView == null) {
            opponentUserView = new OpponentUserView(this, findViewById(R.id.match));
        }
        if (matchId == -1) {
            Log.w(TAG, "empty match id");
            finish();
        } else {
            init(matchId);
        }
    }

    private void init(Long matchId) {
        new MatchDataManager(this).match(matchId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .compose(this.<Match>bindToLifecycle())
                .subscribe(new EmptySubscriber<Match>() {
                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Unable to load match", e);
                    }

                    @Override
                    public void onNext(Match match) {
                        renderMatch(match);
                    }
                });
    }

    private void renderMatch(Match match) {
        User opponentUser = match.getOpponent().getUser();
        opponentUserView.render(opponentUser);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(opponentUser.getName());
        }
    }

    public static Intent createIntent(Context ctx, Long matchId) {
        Intent intent = new Intent(ctx, MatchActivity.class);
        intent.putExtra(MATCH_ID, matchId);
        return intent;
    }
}
