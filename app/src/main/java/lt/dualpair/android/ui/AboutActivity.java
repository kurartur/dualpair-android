package lt.dualpair.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import lt.dualpair.android.R;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, AboutActivity.class);
    }
}
