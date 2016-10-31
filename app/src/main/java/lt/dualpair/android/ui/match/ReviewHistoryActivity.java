package lt.dualpair.android.ui.match;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import lt.dualpair.android.R;
import lt.dualpair.android.ui.BaseActivity;

public class ReviewHistoryActivity extends BaseActivity {

    private final static String MATCH_LIST_FRAGMENT = "MatchListFragment";

    private MatchListFragment matchListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_history_layout);

        setupActionBar(true, getResources().getString(R.string.history));

        FragmentManager fm = getSupportFragmentManager();
        matchListFragment = (MatchListFragment)fm.findFragmentByTag(MATCH_LIST_FRAGMENT);
        if (matchListFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            matchListFragment = new ReviewHistoryMatchListFragment();
            ft.add(android.R.id.content, matchListFragment, MATCH_LIST_FRAGMENT);
            ft.commit();
        }

    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, ReviewHistoryActivity.class);
    }

}
