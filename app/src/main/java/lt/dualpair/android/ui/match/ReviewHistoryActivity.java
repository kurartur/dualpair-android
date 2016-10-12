package lt.dualpair.android.ui.match;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import lt.dualpair.android.R;
import lt.dualpair.android.ui.BaseActivity;

public class ReviewHistoryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_history_layout);
    }

    public static Intent createIntent(Activity activity) {
        Intent intent = new Intent(activity, ReviewHistoryActivity.class);
        return intent;
    }

}
