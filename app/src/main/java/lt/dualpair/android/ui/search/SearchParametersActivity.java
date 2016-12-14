package lt.dualpair.android.ui.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import com.edmodo.rangebar.RangeBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.utils.ToastUtils;

public class SearchParametersActivity extends BaseActivity {

    private static final String TAG = "SearchParamActivity";

    @Bind(R.id.checkbox_search_for_male) CheckBox searchMale;
    @Bind(R.id.checkbox_search_for_female) CheckBox searchFemale;
    @Bind(R.id.age_range_bar) RangeBar ageRangeBar;
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
            public void onIndexChangeListener(RangeBar rangeBar, int i, int i1) {

            }
        });
    }

    public void render(String error) {
        ToastUtils.show(this, error);
    }

    public void render(boolean searchMale, boolean searchFemale, int minAge, int maxAge) {
        progressBar.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        this.searchMale.setChecked(searchMale);
        this.searchFemale.setChecked(searchFemale);
        ageRangeBar.setThumbIndices(minAge - SearchParametersPresenter.MIN_SEARCH_AGE, maxAge - SearchParametersPresenter.MIN_SEARCH_AGE);
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
        boolean searchMale = this.searchMale.isChecked();
        boolean searchFemale = this.searchFemale.isChecked();
        int minAge = ageRangeBar.getLeftIndex() + SearchParametersPresenter.MIN_SEARCH_AGE;
        int maxAge = ageRangeBar.getRightIndex() + SearchParametersPresenter.MIN_SEARCH_AGE;
        presenter.save(searchMale, searchFemale, minAge, maxAge);
        Intent resultData = new Intent();
        resultData.putExtra("SEARCH_MALE", searchMale);
        resultData.putExtra("SEARCH_FEMALE", searchFemale);
        resultData.putExtra("MIN_AGE", minAge);
        resultData.putExtra("SEARCH_FEMALE", maxAge);
        setResult(Activity.RESULT_OK, resultData);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                save();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        save();
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, SearchParametersActivity.class);
    }

}
