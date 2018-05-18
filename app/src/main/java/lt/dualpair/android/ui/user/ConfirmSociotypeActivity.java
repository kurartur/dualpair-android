package lt.dualpair.android.ui.user;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.UserDataManager;
import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.utils.DrawableUtils;

public class ConfirmSociotypeActivity extends BaseActivity {

    private static final String TAG = ConfirmSociotypeActivity.class.getName();
    public static final String PARAM_SOCIOTYPE = "sociotype";
    private static final int MENU_ITEM_OK = 1;

    @Bind(R.id.link)
    TextView link;

    private ConfirmSociotypeViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_confirm_sociotype);

        Sociotype sociotype = (Sociotype)getIntent().getSerializableExtra(PARAM_SOCIOTYPE);

        String title = sociotype.getCode1() + " - ";
        title += getString(getResources().getIdentifier(sociotype.getCode1().toLowerCase() + "_title", "string", getPackageName()));

        setupActionBar(true, title);

        ButterKnife.bind(this);

        ConfirmSociotypeViewModelFactory factory = new ConfirmSociotypeViewModelFactory(getApplication(), sociotype);
        viewModel = ViewModelProviders.of(this, factory).get(ConfirmSociotypeViewModel.class);
        subscribeUi();
    }

    private void subscribeUi() {
        loadSociotype(viewModel.getSociotype());
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
                checkAndUpdateUserSociotypes();
                return true;
        }
        return false;
    }

    private void checkAndUpdateUserSociotypes() {
        final Sociotype sociotype = viewModel.getSociotype();
        viewModel.getCurrentSociotypes().observe(this, new Observer<Set<Sociotype>>() {
            @Override
            public void onChanged(@Nullable Set<Sociotype> currentSociotypes) {
                if (currentSociotypes.size() > 1) {
                    if (currentSociotypes.contains(sociotype)) {
                        // TODO you already have this sociotype, leave only this one?
                    } else {
                        // TODO leave only this one?
                    }
                } else if (currentSociotypes.size() == 1 && currentSociotypes.contains(sociotype)) {
                    // TODO already have this
                } else {
                    Set<Sociotype> newSociotypes = new HashSet<>();
                    newSociotypes.add(sociotype);
                    viewModel.saveSociotypes(newSociotypes).subscribe(new EmptySubscriber() {
                        @Override
                        public void onCompleted() {
                            setResult(Activity.RESULT_OK);
                            finish();
                        }
                    });
                }
            }
        });
    }

    public static Intent createIntent(Activity activity, Sociotype sociotype) {
        Intent intent = new Intent(activity, ConfirmSociotypeActivity.class);
        intent.putExtra(PARAM_SOCIOTYPE, sociotype);
        return intent;
    }

    private static class ConfirmSociotypeViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

        private Application application;
        private Sociotype sociotype;

        public ConfirmSociotypeViewModelFactory(@NonNull Application application, Sociotype sociotype) {
            super(application);
            this.application = application;
            this.sociotype = sociotype;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ConfirmSociotypeViewModel.class)) {
                return (T) new ConfirmSociotypeViewModel(new UserDataManager(application), sociotype);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
