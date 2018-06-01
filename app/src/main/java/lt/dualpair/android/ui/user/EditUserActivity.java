package lt.dualpair.android.ui.user;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.PurposeOfBeing;
import lt.dualpair.android.data.local.entity.RelationshipStatus;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserPurposeOfBeing;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.utils.DrawableUtils;
import lt.dualpair.android.utils.LabelUtils;

public class EditUserActivity extends BaseActivity {

    private static final String TAG = EditUserActivity.class.getName();
    private static final int MENU_ITEM_SAVE = 1;

    @Bind(R.id.name) EditText name;
    @Bind(R.id.date_of_birth) EditText dateOfBirth;
    @Bind(R.id.relationship_status) AutoCompleteTextView relationshipStatus;
    @Bind(R.id.purposes_of_being) LinearLayoutWithAdapter purposesOfBeing;
    @Bind(R.id.description) EditText description;
    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.main_layout) View mainLayout;

    private MenuItem saveMenuItem;

    private PurposeOfBeingAdapter purposeOfBeingAdapter;

    private EditUserViewModel viewModel;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private RelationshipStatus selectedRelationshipStatus;
    private List<PurposeOfBeing> selectedPurposesOfBeing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar(true, getString(R.string.you));
        setContentView(R.layout.edit_user_layout);
        ButterKnife.bind(this);

        showProgress();

        setupRelationshipStatusControl();
        setupPurposeOfBeingControl();

        viewModel = ViewModelProviders.of(this, new EditUserViewModel.Factory(getApplication())).get(EditUserViewModel.class);
        subscribeUi();
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.GONE);
    }

    private void showMain() {
        progressBar.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
    }

    private void setupPurposeOfBeingControl() {
        purposeOfBeingAdapter = new PurposeOfBeingAdapter(this, new PurposeOfBeingAdapter.OnPurposeListChangeListener() {
            @Override
            public void onChange(List<PurposeOfBeing> purposes) {
                selectedPurposesOfBeing = purposes;
            }
        });
        purposesOfBeing.setAdapter(purposeOfBeingAdapter);
    }

    private void setupRelationshipStatusControl() {
        relationshipStatus.setAdapter(new RelationshipStatusAdapter(this, this, R.layout.text_view_layout, RelationshipStatus.values()));
        relationshipStatus.setKeyListener(null);
        relationshipStatus.setOnTouchListener((v, event) -> {
            ((AutoCompleteTextView) v).showDropDown();
            return false;
        });
        relationshipStatus.setOnItemClickListener((parent, view, position, id) -> {
            RelationshipStatus relStatus = (RelationshipStatus)parent.getItemAtPosition(position);
            relationshipStatus.setText(LabelUtils.getRelationshipStatusLabel(EditUserActivity.this, relStatus));
            selectedRelationshipStatus = relStatus;
        });
    }

    private void subscribeUi() {
        viewModel.getData().observe(this, new Observer<EditUserViewModel.UserData>() {
            @Override
            public void onChanged(@Nullable EditUserViewModel.UserData userData) {
                renderUser(userData.getUser());
                renderPurposesOfBeing(userData.getPurposeOfBeings());
                showMain();
                saveMenuItem.setVisible(true);
            }
        });
    }

    private void renderUser(User u) {
        this.name.setText(u.getName());
        this.dateOfBirth.setText(DATE_FORMAT.format(u.getDateOfBirth()));
        this.description.setText(u.getDescription());
        RelationshipStatus relationshipStatus = u.getRelationshipStatus();
        selectedRelationshipStatus = relationshipStatus;
        this.relationshipStatus.setText(LabelUtils.getRelationshipStatusLabel(this, relationshipStatus));
    }

    public void renderPurposesOfBeing(List<UserPurposeOfBeing> userPurposesOfBeing) {
        if (userPurposesOfBeing != null) {
            List<PurposeOfBeing> purposesOfBeings = new ArrayList<>();
            for (UserPurposeOfBeing userPurposeOfBeing : userPurposesOfBeing) {
                purposesOfBeings.add(userPurposeOfBeing.getPurpose());
            }
            this.selectedPurposesOfBeing = purposesOfBeings;
            this.purposeOfBeingAdapter.setCheckedPurposes(purposesOfBeings);
        }
    }

    public void onSaved() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        saveMenuItem = menu.add(Menu.NONE, MENU_ITEM_SAVE, Menu.NONE, R.string.save);
        saveMenuItem.setIcon(DrawableUtils.getActionBarIcon(this, R.drawable.ic_done_black_48dp));
        saveMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        saveMenuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) return true;
        switch (item.getItemId()) {
            case MENU_ITEM_SAVE:
                saveMenuItem.setActionView(R.layout.action_progressbar);
                try {
                    viewModel.save(
                                name.getText().toString(),
                                DATE_FORMAT.parse(dateOfBirth.getText().toString()),
                                description.getText().toString(),
                                selectedRelationshipStatus,
                                selectedPurposesOfBeing
                            )
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Action() {
                                @Override
                                public void run() {
                                    onSaved();
                                }
                            });
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                return true;
        }
        return false;
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, EditUserActivity.class);
    }

}
