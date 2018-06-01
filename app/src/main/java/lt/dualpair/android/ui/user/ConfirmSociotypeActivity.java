package lt.dualpair.android.ui.user;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.Sociotype;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.utils.DrawableUtils;

public class ConfirmSociotypeActivity extends BaseActivity {

    private static final String TAG = ConfirmSociotypeActivity.class.getName();
    public static final String PARAM_SOCIOTYPE = "sociotype";
    private static final int MENU_ITEM_OK = 1;

    @Bind(R.id.link)
    TextView link;

    private ConfirmSociotypeViewModel viewModel;
    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_confirm_sociotype);

        String sociotypeCode = getIntent().getStringExtra(PARAM_SOCIOTYPE);

        String title = sociotypeCode + " - ";
        title += getString(getResources().getIdentifier(sociotypeCode.toLowerCase() + "_title", "string", getPackageName()));

        setupActionBar(true, title);

        ButterKnife.bind(this);

        ConfirmSociotypeViewModel.ConfirmSociotypeViewModelFactory factory = new ConfirmSociotypeViewModel.ConfirmSociotypeViewModelFactory(getApplication());
        viewModel = ViewModelProviders.of(this, factory).get(ConfirmSociotypeViewModel.class);
        subscribeUi(sociotypeCode);
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposable.clear();
    }

    private void subscribeUi(String sociotypeCode) {
        viewModel.getSociotype(sociotypeCode).observe(this, this::loadSociotype);
    }

    private void loadSociotype(Sociotype sociotype) {
        String url = "http://www.sociotype.com/socionics/types/"
                + sociotype.getCode1()
                + "-"
                + sociotype.getCode2().substring(0, 3)
                + sociotype.getCode2().substring(3).toLowerCase()
                + "/";
        link.setText(Html.fromHtml("<a href=\"" + url + "\">" + url + "</a>"));
        link.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem okMenuItem = menu.add(Menu.NONE, MENU_ITEM_OK, Menu.NONE, R.string.ok);
        okMenuItem.setIcon(DrawableUtils.getActionBarIcon(this, R.drawable.ic_done_black_48dp));
        okMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) return true;
        switch (item.getItemId()) {
            case MENU_ITEM_OK:
                updateUserSociotypes();
                return true;
        }
        return false;
    }

    private void updateUserSociotypes() {
        String sociotypeCode = getIntent().getStringExtra(PARAM_SOCIOTYPE);
        disposable.add(
                viewModel.saveSociotype(sociotypeCode).subscribe(() -> {
                    setResult(Activity.RESULT_OK);
                    finish();
                })
        );
    }

    public static Intent createIntent(Activity activity, String code1) {
        Intent intent = new Intent(activity, ConfirmSociotypeActivity.class);
        intent.putExtra(PARAM_SOCIOTYPE, code1);
        return intent;
    }

}
