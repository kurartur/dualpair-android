package lt.dualpair.android.ui.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.ProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.SearchParameters;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.utils.DrawableUtils;
import lt.dualpair.android.utils.ToastUtils;

public class SearchParametersActivity extends BaseActivity {

    private static final String TAG = SearchParametersActivity.class.getName();
    public static final String RESULT_BUNDLE_KEY = "RESULT_BUNDLE";
    public static final String SEARCH_PARAMETERS_KEY = "SEARCH_PARAMETERS";
    private static final String WITH_HOME_BUTTON = "WITH_HOME_BUTTON";
    private static final int MENU_ITEM_OK = 1;

    @Bind(R.id.checkbox_search_for_male) CheckBox searchMale;
    @Bind(R.id.checkbox_search_for_female) CheckBox searchFemale;
    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.main_layout) View mainLayout;
    @Bind(R.id.min_age_picker) NumberPicker minAgePicker;
    @Bind(R.id.max_age_picker) NumberPicker maxAgePicker;

    private static SearchParametersPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_parameters_layout);

        boolean homeButton = getIntent().getBooleanExtra(WITH_HOME_BUTTON, true);
        setupActionBar(homeButton, getResources().getString(R.string.search_parameters));

        ButterKnife.bind(this);

        mainLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        if (presenter == null || savedInstanceState == null) {
            presenter = new SearchParametersPresenter(this);
        } else {
            presenter = new SearchParametersPresenter(savedInstanceState);
        }
        presenter.onTakeView(this);

        minAgePicker.setMinValue(SearchParametersPresenter.MIN_SEARCH_AGE);
        minAgePicker.setMaxValue(SearchParametersPresenter.MAX_SEARCH_AGE);
        minAgePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                if (newVal > maxAgePicker.getValue()) {
                    maxAgePicker.setValue(newVal);
                }
            }
        });
        maxAgePicker.setMinValue(SearchParametersPresenter.MIN_SEARCH_AGE);
        maxAgePicker.setMaxValue(SearchParametersPresenter.MAX_SEARCH_AGE);
        maxAgePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                if (newVal < minAgePicker.getValue()) {
                    minAgePicker.setValue(newVal);
                }
            }
        });
    }

    public void render(String error) {
        ToastUtils.show(this, error);
    }

    public void render(SearchParameters searchParameters) {
        this.searchMale.setChecked(searchParameters.getSearchMale());
        this.searchFemale.setChecked(searchParameters.getSearchFemale());
        minAgePicker.setValue(searchParameters.getMinAge());
        maxAgePicker.setValue(searchParameters.getMaxAge());
        progressBar.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
    }

    public void onSaveError(String error) {
        progressBar.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        ToastUtils.show(this, error);
    }

    public void onSaved(SearchParameters searchParameters) {
        Intent resultData = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(SEARCH_PARAMETERS_KEY, searchParameters);
        resultData.putExtra(RESULT_BUNDLE_KEY, bundle);
        setResult(Activity.RESULT_OK, resultData);
        finish();
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

    private void save() {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setSearchMale(searchMale.isChecked());
        searchParameters.setSearchFemale(searchFemale.isChecked());
        searchParameters.setMinAge(minAgePicker.getValue());
        searchParameters.setMaxAge(maxAgePicker.getValue());
        presenter.save(searchParameters);
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
