package lt.dualpair.android.ui.user;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.Sociotype;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.ui.socionics.SocionicsTestActivity;

public class AddSociotypeActivity extends BaseActivity {

    private static final int CONFIRM_REQUEST_CODE = 1;
    private static final String WITH_HOME_BUTTON = "WITH_HOME_BUTTON";

    @Bind(R.id.button_start_test)
    LinearLayout startTestButton;

    @Bind(R.id.grid_sociotypes)
    GridLayout gridLayout;

    private AddSociotypeViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_sociotype);

        boolean homeButton = getIntent().getBooleanExtra(WITH_HOME_BUTTON, true);
        setupActionBar(homeButton, getResources().getString(R.string.choose_sociotype));

        ButterKnife.bind(this);

        startTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTest();
            }
        });

        viewModel = ViewModelProviders.of(this, new AddSociotypeViewModel.Factory(getApplication())).get(AddSociotypeViewModel.class);
        subscribeUi(viewModel);
    }

    private void subscribeUi(AddSociotypeViewModel viewModel) {
        viewModel.getSociotypes().observe(this, new Observer<List<Sociotype>>() {
            @Override
            public void onChanged(@Nullable List<Sociotype> sociotypes) {
                if (sociotypes != null) {
                    fillGrid(sociotypes);
                }
            }
        });
    }

    private void openTest() {
        Intent intent = new Intent(this, SocionicsTestActivity.class);
        startActivity(intent);
    }

    private void fillGrid(List<Sociotype> sociotypes) {
        for (final Sociotype sociotype : sociotypes) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_grid_sociotype, gridLayout, false);
            TextView code = view.findViewById(R.id.sociotype_code);
            code.setText(sociotype.getCode1() + " (" + sociotype.getCode2() + ")");
            TextView title = view.findViewById(R.id.sociotype_title);
            title.setText(getResources().getString(getResources().getIdentifier(sociotype.getCode1().toLowerCase() + "_title", "string", getPackageName())));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openConfirmation(sociotype);
                }
            });
            gridLayout.addView(view);
        }
    }

    private void openConfirmation(Sociotype sociotype) {
        startActivityForResult(ConfirmSociotypeActivity.createIntent(this, sociotype.getCode1()), CONFIRM_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CONFIRM_REQUEST_CODE:
                if (Activity.RESULT_OK == resultCode) {
                    setResult(Activity.RESULT_OK);
                    finish();
                }
                break;
        }
    }



    public static Intent createIntent(Activity activity, boolean homeButton) {
        Intent intent = new Intent(activity, AddSociotypeActivity.class);
        intent.putExtra(WITH_HOME_BUTTON, homeButton);
        return intent;
    }
}
