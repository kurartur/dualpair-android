package com.artur.dualpair.android.ui.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.artur.dualpair.android.R;
import com.artur.dualpair.android.core.user.SetSearchParametersTask;
import com.artur.dualpair.android.dto.SearchParameters;
import com.artur.dualpair.android.rx.EmptySubscriber;
import com.artur.dualpair.android.ui.BaseActivity;
import com.artur.dualpair.android.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchParametersActivity extends BaseActivity {

    private static final String TAG = "SearchParamActivity";

    @Bind(R.id.checkbox_search_for_male)
    CheckBox searchMale;

    @Bind(R.id.checkbox_search_for_female)
    CheckBox searchFemale;

    @Bind(R.id.edit_text_min_age)
    EditText minAge;

    @Bind(R.id.edit_text_max_age)
    EditText maxAge;

    @Bind(R.id.button_submit)
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_search_parameters);
        ButterKnife.bind(this);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postSearchParameters();
            }
        });
    }

    private void postSearchParameters() {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setSearchMale(searchMale.isChecked());
        searchParameters.setSearchFemale(searchFemale.isChecked());
        searchParameters.setMinAge(Integer.valueOf(minAge.getText().toString()));
        searchParameters.setMaxAge(Integer.valueOf(maxAge.getText().toString()));
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

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, SearchParametersActivity.class);
    }
}
