package lt.dualpair.android.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.utils.DrawableUtils;
import lt.dualpair.android.utils.ToastUtils;

public class EditUserActivity extends BaseActivity {

    private static final String TAG = "EditUserActivity";
    private static final int MENU_ITEM_SAVE = 1;

    @Bind(R.id.name) EditText name;
    @Bind(R.id.date_of_birth) EditText dateOfBirth;
    @Bind(R.id.description) EditText description;
    @Bind(R.id.description_input_layout) TextInputLayout descriptionInputLayout;
    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.main_layout) View mainLayout;

    private static EditUserPresenter presenter;

    private MenuItem saveMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar(true, getString(R.string.you));
        setContentView(R.layout.edit_user_layout);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(DrawableUtils.getActionBarIcon(this, R.drawable.ic_close_black_30dp));
        }

        if (presenter == null || savedInstanceState == null) {
            presenter = new EditUserPresenter(this);
        } else {
            presenter = new EditUserPresenter(savedInstanceState);
        }
        presenter.onTakeView(this);

        description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    descriptionInputLayout.setHint(getString(R.string.about_you));
                    ((EditText)v).setHint(getString(R.string.add_description));
                } else {
                    descriptionInputLayout.setHint(getString(R.string.add_description));
                    ((EditText)v).setHint("");
                }
            }
        });

    }

    public void onSaved() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    public void render(String name, String dateOfBirth, String description) {
        progressBar.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        this.name.setText(name);
        this.dateOfBirth.setText(dateOfBirth);
        this.description.setText(description);

        descriptionInputLayout.setHint(TextUtils.isEmpty(description) ? getString(R.string.add_description) : getString(R.string.about_you));
    }

    public void render(String error) {
        progressBar.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        saveMenuItem.setActionView(null);
        ToastUtils.show(this, error);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSave(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onTakeView(null);
        if (!isChangingConfigurations())
            presenter = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        saveMenuItem = menu.add(Menu.NONE, MENU_ITEM_SAVE, Menu.NONE, R.string.save);
        saveMenuItem.setIcon(DrawableUtils.getActionBarIcon(this, R.drawable.ic_done_black_48dp));
        saveMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) return true;
        switch (item.getItemId()) {
            case MENU_ITEM_SAVE:
                saveMenuItem.setActionView(R.layout.action_progressbar);
                presenter.save(name.getText().toString(), dateOfBirth.getText().toString(), description.getText().toString());
                return true;
        }
        return false;
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, EditUserActivity.class);
    }
}
