package lt.dualpair.android.ui.search;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.core.user.GetSearchParametersTask;
import lt.dualpair.android.core.user.SetSearchParametersTask;
import lt.dualpair.android.resource.SearchParameters;
import lt.dualpair.android.rx.EmptySubscriber;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.utils.ToastUtils;

public class SearchParametersActivity extends BaseActivity {

    private static final int MENU_ITEM_SAVE = 1;
    private static final String TAG = "SearchParamActivity";
    private static final Integer MIN_SEARCH_AGE = 13;
    private static final Integer MAX_SEARCH_AGE = 120;

    @Bind(R.id.checkbox_search_for_male)
    CheckBox searchMale;

    @Bind(R.id.checkbox_search_for_female)
    CheckBox searchFemale;

    @Bind(R.id.min_age_spinner)
    Spinner minAge;

    @Bind(R.id.max_age_spinner)
    Spinner maxAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_parameters_layout);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.search_parameters));
            actionBar.setIcon(
                    new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        }

        ButterKnife.bind(this);

        minAge.setAdapter(createAgeSpinnerAdapter());
        maxAge.setAdapter(createAgeSpinnerAdapter());

        loadSearchParameters();
    }

    private void loadSearchParameters() {
        final Activity activity = this;
        new GetSearchParametersTask(activity).execute(new EmptySubscriber<SearchParameters>() {
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Unable to load search parameters", e);
                ToastUtils.show(activity, "Unable to load search parameters");
                finish();
            }

            @Override
            public void onNext(SearchParameters searchParameters) {
                fillSearchParameters(searchParameters);
            }
        }, this);
    }

    private void fillSearchParameters(SearchParameters searchParameters) {
        searchMale.setChecked(searchParameters.getSearchMale());
        searchFemale.setChecked(searchParameters.getSearchFemale());
        if (searchParameters.getMinAge() != null) {
            ArrayAdapter<Integer> adapter = (ArrayAdapter)minAge.getAdapter();
            int spinnerPosition = adapter.getPosition(searchParameters.getMinAge());
            minAge.setSelection(spinnerPosition);
        }
        if (searchParameters.getMaxAge() != null) {
            ArrayAdapter<Integer> adapter = (ArrayAdapter)maxAge.getAdapter();
            int spinnerPosition = adapter.getPosition(searchParameters.getMaxAge());
            maxAge.setSelection(spinnerPosition);
        }
    }

    private void postSearchParameters() {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setSearchMale(searchMale.isChecked());
        searchParameters.setSearchFemale(searchFemale.isChecked());
        searchParameters.setMinAge((Integer)minAge.getSelectedItem());
        searchParameters.setMaxAge((Integer)maxAge.getSelectedItem());
        new SetSearchParametersTask(this, searchParameters).execute(new EmptySubscriber<Void>() {
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Unable to save search parameters", e);
                ToastUtils.show(SearchParametersActivity.this, e.getMessage());
            }

            @Override
            public void onNext(Void aVoid) {
                setResult(RESULT_OK);
                finish();
            }
        }, this);
    }

    private ArrayAdapter<Integer> createAgeSpinnerAdapter() {
        Integer[] items = new Integer[MAX_SEARCH_AGE - MIN_SEARCH_AGE + 1];
        for (int i = MIN_SEARCH_AGE; i <= MAX_SEARCH_AGE; i++) {
            items[i - MIN_SEARCH_AGE] = i;
        }
        return new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ITEM_SAVE, Menu.NONE, R.string.save)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case MENU_ITEM_SAVE:
                postSearchParameters();
                finish(); // TODO finish after saving or show progress
                return false;
        }
        return false;
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, SearchParametersActivity.class);
    }
}
