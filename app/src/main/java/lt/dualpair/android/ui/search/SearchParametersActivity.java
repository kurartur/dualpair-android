package lt.dualpair.android.ui.search;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.ProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.UserSearchParameters;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.utils.DrawableUtils;

public class SearchParametersActivity extends BaseActivity {

    private static final String TAG = SearchParametersActivity.class.getName();
    private static final String WITH_HOME_BUTTON = "WITH_HOME_BUTTON";
    private static final int MENU_ITEM_OK = 1;

    @Bind(R.id.checkbox_search_for_male) CheckBox searchMale;
    @Bind(R.id.checkbox_search_for_female) CheckBox searchFemale;
    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.main_layout) View mainLayout;
    @Bind(R.id.min_age_picker) NumberPicker minAgePicker;
    @Bind(R.id.max_age_picker) NumberPicker maxAgePicker;

    public static final Integer MIN_SEARCH_AGE = 18;
    public static final Integer MAX_SEARCH_AGE = 110;

    private SearchParametersViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_parameters_layout);

        boolean homeButton = getIntent().getBooleanExtra(WITH_HOME_BUTTON, true);
        setupActionBar(homeButton, getResources().getString(R.string.search_parameters));

        ButterKnife.bind(this);

        mainLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        minAgePicker.setMinValue(MIN_SEARCH_AGE);
        minAgePicker.setMaxValue(MAX_SEARCH_AGE);
        minAgePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                if (newVal > maxAgePicker.getValue()) {
                    maxAgePicker.setValue(newVal);
                }
            }
        });
        maxAgePicker.setMinValue(MIN_SEARCH_AGE);
        maxAgePicker.setMaxValue(MAX_SEARCH_AGE);
        maxAgePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                if (newVal < minAgePicker.getValue()) {
                    minAgePicker.setValue(newVal);
                }
            }
        });

        viewModel = ViewModelProviders.of(this, new SearchParametersViewModel.Factory(getApplication())).get(SearchParametersViewModel.class);
        subscribeUi();
    }

    private void subscribeUi() {
        viewModel.getSearchParameters().observe(this, new Observer<UserSearchParameters>() {
            @Override
            public void onChanged(@Nullable UserSearchParameters searchParameters) {
                render(searchParameters);
            }
        });
    }

    public void render(UserSearchParameters searchParameters) {
        this.searchMale.setChecked(searchParameters.getSearchMale());
        this.searchFemale.setChecked(searchParameters.getSearchFemale());
        minAgePicker.setValue(searchParameters.getMinAge());
        maxAgePicker.setValue(searchParameters.getMaxAge());
        progressBar.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
    }

    public void onSaved() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void save() {
        UserSearchParameters searchParameters = new UserSearchParameters();
        searchParameters.setSearchMale(searchMale.isChecked());
        searchParameters.setSearchFemale(searchFemale.isChecked());
        searchParameters.setMinAge(minAgePicker.getValue());
        searchParameters.setMaxAge(maxAgePicker.getValue());
        viewModel.save(searchParameters)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::onSaved);
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
                save();
                return true;
        }
        return false;
    }

    public static Intent createIntent(Activity activity, boolean homeButton) {
        Intent intent = new Intent(activity, SearchParametersActivity.class);
        intent.putExtra(WITH_HOME_BUTTON, homeButton);
        return intent;
    }

}
