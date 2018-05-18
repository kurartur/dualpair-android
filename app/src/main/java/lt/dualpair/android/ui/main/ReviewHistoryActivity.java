package lt.dualpair.android.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import lt.dualpair.android.R;
import lt.dualpair.android.ui.BaseActivity;

public class ReviewHistoryActivity extends BaseActivity {

    private final static String REVIEW_HISTORY_FRAGMENT = "ReviewHistoryFragment";

    private ReviewHistoryFragment reviewHistoryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_history_layout);

        setupActionBar(true, getResources().getString(R.string.history));

        FragmentManager fm = getSupportFragmentManager();
        reviewHistoryFragment = (ReviewHistoryFragment)fm.findFragmentByTag(REVIEW_HISTORY_FRAGMENT);
        if (reviewHistoryFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            reviewHistoryFragment = new ReviewHistoryFragment();
            ft.add(android.R.id.content, reviewHistoryFragment, REVIEW_HISTORY_FRAGMENT);
            ft.commit();
        }

    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, ReviewHistoryActivity.class);
    }

}
