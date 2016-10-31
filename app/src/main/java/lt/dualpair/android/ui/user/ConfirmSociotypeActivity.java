package lt.dualpair.android.ui.user;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
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
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.utils.ToastUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ConfirmSociotypeActivity extends BaseActivity {

    private static final String TAG = "ConfirmSocActivity";
    public static final String PARAM_SOCIOTYPE = "sociotype";
    private static final int MENU_ITEM_OK = 1;

    @Bind(R.id.header)
    TextView header;

    private Sociotype sociotype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_confirm_sociotype);

        Sociotype sociotype = (Sociotype)getIntent().getSerializableExtra(PARAM_SOCIOTYPE);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(sociotype.getCode1());
            actionBar.setIcon(
                    new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        }
        setupActionBar(true, sociotype.getCode1());

        ButterKnife.bind(this);

        loadSociotype(sociotype);
    }

    private void updateUserSociotypes() {
        new UserDataManager(this).getUser()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .compose(this.<User>bindToLifecycle())
            .subscribe(new EmptySubscriber<User>() {
                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "Unable to get user", e);
                    ToastUtils.show(ConfirmSociotypeActivity.this, e.getMessage());
                }

                @Override
                public void onNext(User user) {
                    unsubscribe();
                    Set<Sociotype> currentSociotypes = user.getSociotypes();
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
                        setSociotypes(newSociotypes);
                    }
                }
            });
    }

    private void setSociotypes(Set<Sociotype> sociotypes) {
        new UserDataManager(this).setSociotypes(sociotypes)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .compose(this.<User>bindToLifecycle())
                .subscribe(new EmptySubscriber<User>() {
                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Unable to set sociotypes", e);
                        ToastUtils.show(ConfirmSociotypeActivity.this, e.getMessage());
                    }

                    @Override
                    public void onNext(User u) {
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                });
    }

    private void loadSociotype(Sociotype sociotype) {
        this.sociotype = sociotype;
        header.setText(sociotype.getCode1());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ITEM_OK, Menu.NONE, R.string.ok)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
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

    public static Intent createIntent(Activity activity, Sociotype sociotype) {
        Intent intent = new Intent(activity, ConfirmSociotypeActivity.class);
        intent.putExtra(PARAM_SOCIOTYPE, sociotype);
        return intent;
    }
}
