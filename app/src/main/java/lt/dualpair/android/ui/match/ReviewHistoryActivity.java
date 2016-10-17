package lt.dualpair.android.ui.match;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import lt.dualpair.android.R;
import lt.dualpair.android.ui.BaseActivity;

public class ReviewHistoryActivity extends BaseActivity {

    private final static String MATCH_LIST_FRAGMENT = "MatchListFragment";

    private MatchListFragment matchListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_history_layout);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.history));
            actionBar.setIcon(
                    new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        }

        FragmentManager fm = getFragmentManager();
        matchListFragment = (MatchListFragment)fm.findFragmentByTag(MATCH_LIST_FRAGMENT);
        if (matchListFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            matchListFragment = new ReviewHistoryMatchListFragment();
            ft.add(android.R.id.content, matchListFragment, MATCH_LIST_FRAGMENT);
            ft.commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, ReviewHistoryActivity.class);
    }

}
