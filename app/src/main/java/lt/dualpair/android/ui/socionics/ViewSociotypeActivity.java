package lt.dualpair.android.ui.socionics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import lt.dualpair.android.data.local.entity.Sociotype;
import lt.dualpair.android.ui.BaseActivity;

public class ViewSociotypeActivity extends BaseActivity {

    private static final String ARG_SOCIOTYPE = "sociotype";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Sociotype sociotype = (Sociotype)getIntent().getSerializableExtra(ARG_SOCIOTYPE);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, SociotypeDetailsFragment.newInstance(sociotype));
            ft.commit();
        }
    }

    public static Intent createIntent(Activity activity, Sociotype sociotype) {
        Intent intent = new Intent(activity, ViewSociotypeActivity.class);
        intent.putExtra(ARG_SOCIOTYPE, sociotype);
        return intent;
    }
}
