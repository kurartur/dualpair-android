package lt.dualpair.android.ui.socionics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.Sociotype;
import lt.dualpair.android.ui.BaseActivity;

public class SetSociotypeActivity extends BaseActivity implements SociotypeListRecyclerAdapter.OnSociotypeClickListener {

    private static final String TAG = SetSociotypeActivity.class.getName();

    private static final String ARG_FIRST = "first";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sociotypes_layout);

        if (savedInstanceState == null) {
            boolean firstTime = getIntent().getBooleanExtra(ARG_FIRST, false);
            SociotypeListFragment sociotypeListFragment = SociotypeListFragment.newInstance(!firstTime);

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, sociotypeListFragment).commit();
        }

    }

    @Override
    public void onSociotypeClick(Sociotype sociotype) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ConfirmableSociotypeDetailsFragment sociotypeDetailsFragment = ConfirmableSociotypeDetailsFragment.newInstance(sociotype);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fragment_container, sociotypeDetailsFragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    public static Intent createIntent(Activity activity, boolean firstTime) {
        Intent intent = new Intent(activity, SetSociotypeActivity.class);
        intent.putExtra(ARG_FIRST, firstTime);
        return intent;
    }

}
