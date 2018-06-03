package lt.dualpair.android.ui.user;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.PurposeOfBeing;
import lt.dualpair.android.data.local.entity.RelationshipStatus;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserPurposeOfBeing;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.utils.DrawableUtils;
import lt.dualpair.android.utils.LabelUtils;
import lt.dualpair.android.utils.ToastUtils;

public class EditUserActivity extends BaseActivity {

    private static final String TAG = EditUserActivity.class.getName();
    private static final int MENU_ITEM_SAVE = 1;
    private static final DateFormat DATE_FORMAT = SimpleDateFormat.getDateInstance();

    private static final int STATE_LOADING = 0;
    private static final int STATE_LOADED = 1;
    private static final int STATE_SAVING = 2;

    @Bind(R.id.name) EditText name;
    @Bind(R.id.name_length) TextView nameLength;
    @Bind(R.id.date_of_birth) EditText dateOfBirth;
    @Bind(R.id.relationship_status) AutoCompleteTextView relationshipStatus;
    @Bind(R.id.purposes_of_being) LinearLayoutWithAdapter purposesOfBeing;
    @Bind(R.id.description) EditText description;
    @Bind(R.id.description_length) TextView descriptionLength;
    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.main_layout) View mainLayout;

    private int state = 0;

    private PurposeOfBeingAdapter purposeOfBeingAdapter;

    private EditUserViewModel viewModel;

    private Calendar dateOfBirthCalendar = Calendar.getInstance();

    private RelationshipStatus selectedRelationshipStatus;
    private List<PurposeOfBeing> selectedPurposesOfBeing;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar(true, getString(R.string.you));
        setContentView(R.layout.edit_user_layout);
        ButterKnife.bind(this);

        setState(STATE_LOADING);

        setupNameControl();
        setupDescriptionControl();
        setupDateOfBirthControl();
        setupRelationshipStatusControl();
        setupPurposeOfBeingControl();

        viewModel = ViewModelProviders.of(this, new EditUserViewModel.Factory(getApplication())).get(EditUserViewModel.class);
        subscribeUi();
    }

    private void setState(int state) {
        this.state = state;
        invalidateOptionsMenu();
        if (state == STATE_LOADING) {
            progressBar.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.GONE);
        } else if (state == STATE_LOADED) {
            progressBar.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
        } else if (state == STATE_SAVING) {}
    }

    private void setupDescriptionControl() {
        setCurrentDescriptionLength(0);
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String description = s.toString();
                setCurrentDescriptionLength(description.length());
            }
        });
        description.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                descriptionLength.setVisibility(View.VISIBLE);
            } else {
                descriptionLength.setVisibility(View.GONE);
            }
        });
    }

    private void setCurrentDescriptionLength(int length) {
        descriptionLength.setText(getString(R.string.some_out_of_some, length, getResources().getInteger(R.integer.description_length)));
    }

    private void setupNameControl() {
        setCurrentNameLength(0);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String name = s.toString();
                setCurrentNameLength(name.length());
                if (name.length() == 0) {
                    EditUserActivity.this.name.setError(getString(R.string.name_cant_be_empty));
                } else {
                    EditUserActivity.this.name.setError(null);
                }
            }
        });
        name.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                nameLength.setVisibility(View.VISIBLE);
            } else {
                nameLength.setVisibility(View.GONE);
            }
        });
    }

    private void setCurrentNameLength(int length) {
        nameLength.setText(getString(R.string.some_out_of_some, length, getResources().getInteger(R.integer.name_length)));
    }

    private void setupDateOfBirthControl() {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateOfBirthCalendar.set(Calendar.YEAR, year);
                dateOfBirthCalendar.set(Calendar.MONTH, month);
                dateOfBirthCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                renderDateOfBirthFromCalendar();
            }
        };
        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditUserActivity.this, onDateSetListener, dateOfBirthCalendar
                        .get(Calendar.YEAR), dateOfBirthCalendar.get(Calendar.MONTH),
                        dateOfBirthCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void renderDateOfBirthFromCalendar() {
        dateOfBirth.setText(DATE_FORMAT.format(dateOfBirthCalendar.getTime()));
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
                setState(STATE_LOADED);
            }
        });
    }

    private void renderUser(User u) {
        this.name.setText(u.getName());
        this.dateOfBirthCalendar.setTime(u.getDateOfBirth());
        renderDateOfBirthFromCalendar();
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (state == STATE_LOADED) {
            MenuItem saveMenuItem = menu.add(Menu.NONE, MENU_ITEM_SAVE, Menu.NONE, R.string.save);
            saveMenuItem.setIcon(DrawableUtils.getActionBarIcon(this, R.drawable.ic_done_black_48dp));
            saveMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        } if (state == STATE_SAVING) {
            MenuItem saving = menu.add(Menu.NONE, 0, Menu.NONE, "");
            saving.setActionView(R.layout.action_progressbar);
            saving.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) return true;
        switch (item.getItemId()) {
            case MENU_ITEM_SAVE:
                save();
                return true;
        }
        return false;
    }

    private void save() {
        if (validate()) {
            setState(STATE_SAVING);
            disposable.add(
                    viewModel.save(
                            name.getText().toString(),
                            dateOfBirthCalendar.getTime(),
                            description.getText().toString(),
                            selectedRelationshipStatus,
                            selectedPurposesOfBeing
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(() -> {
                        setResult(Activity.RESULT_OK);
                        finish();
                    }, throwable -> {
                        ToastUtils.show(EditUserActivity.this, getString(R.string.unable_to_save_user));
                        setState(STATE_LOADED);
                    })
            );
        }
    }

    private boolean validate() {
        if (name.getError() != null) {
            name.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, EditUserActivity.class);
    }

}
