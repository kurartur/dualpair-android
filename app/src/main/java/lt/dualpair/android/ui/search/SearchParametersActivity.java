package lt.dualpair.android.ui.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edmodo.rangebar.RangeBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.SearchParameters;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.utils.ToastUtils;

public class SearchParametersActivity extends BaseActivity {

    private static final String TAG = "SearchParamActivity";
    public static final String RESULT_BUNDLE_KEY = "RESULT_BUNDLE";
    public static final String SEARCH_PARAMETERS_KEY = "SEARCH_PARAMETERS";

    @Bind(R.id.checkbox_search_for_male) CheckBox searchMale;
    @Bind(R.id.checkbox_search_for_female) CheckBox searchFemale;
    @Bind(R.id.age_range_bar) RangeBar ageRangeBar;
    @Bind(R.id.min_age_text) TextView minAgeText;
    @Bind(R.id.max_age_text) TextView maxAgeText;
    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.main_layout) View mainLayout;

    private static SearchParametersPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_parameters_layout);
        setupActionBar(true, getResources().getString(R.string.search_parameters));
        ButterKnife.bind(this);

        mainLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        if (presenter == null || savedInstanceState == null) {
            presenter = new SearchParametersPresenter(this);
        } else {
            presenter = new SearchParametersPresenter(savedInstanceState);
        }
        presenter.onTakeView(this);

        ageRangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int start, int end) {
                minAgeText.setText(calculateAge(start) + "");
                maxAgeText.setText(calculateAge(end) + "");
            }
        });
    }

    private int calculateAge(int rangePos) {
        return rangePos - 1 + SearchParametersPresenter.MIN_SEARCH_AGE;
    }

    private int calculatePos(int age) {
        return age - SearchParametersPresenter.MIN_SEARCH_AGE + 1;
    }

    public void render(String error) {
        ToastUtils.show(this, error);
    }

    public void render(SearchParameters searchParameters) {
        this.searchMale.setChecked(searchParameters.getSearchMale());
        this.searchFemale.setChecked(searchParameters.getSearchFemale());
        ageRangeBar.setThumbIndices(calculatePos(searchParameters.getMinAge()),
                                    calculatePos(searchParameters.getMaxAge()));
        progressBar.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
    }

    public void onSaveError(String error) {
        progressBar.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        ToastUtils.show(this, error);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
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
        int minAge = calculateAge(ageRangeBar.getLeftIndex());
        int maxAge = calculateAge(ageRangeBar.getRightIndex());
        searchParameters.setMinAge(minAge);
        searchParameters.setMaxAge(maxAge);
        presenter.save(searchParameters);
        Intent resultData = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(SEARCH_PARAMETERS_KEY, searchParameters);
        resultData.putExtra(RESULT_BUNDLE_KEY, bundle);
        setResult(Activity.RESULT_OK, resultData);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                save();
                finish();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        save();
        super.onBackPressed();
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, SearchParametersActivity.class);
    }

}
