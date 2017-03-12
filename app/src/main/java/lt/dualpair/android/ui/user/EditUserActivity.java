package lt.dualpair.android.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.PurposeOfBeing;
import lt.dualpair.android.data.resource.RelationshipStatus;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.utils.DrawableUtils;
import lt.dualpair.android.utils.ToastUtils;

public class EditUserActivity extends BaseActivity implements PurposeOfBeingAdapter.OnPurposeListChangeListener {

    private static final String TAG = "EditUserActivity";
    private static final int MENU_ITEM_SAVE = 1;

    @Bind(R.id.name) EditText name;
    @Bind(R.id.date_of_birth) EditText dateOfBirth;
    @Bind(R.id.relationship_status) AutoCompleteTextView relationshipStatus;
    @Bind(R.id.purposes_of_being) LinearLayoutWithAdapter purposesOfBeing;
    @Bind(R.id.description) EditText description;
    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.main_layout) View mainLayout;

    private static EditUserPresenter presenter;

    private MenuItem saveMenuItem;

    private PurposeOfBeingAdapter purposeOfBeingAdapter;

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

        relationshipStatus.setAdapter(new RelationshipStatusAdapter(this, this, R.layout.text_view_layout, RelationshipStatus.values()));
        relationshipStatus.setKeyListener(null);
        relationshipStatus.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                ((AutoCompleteTextView) v).showDropDown();
                return false;
            }
        });
        relationshipStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RelationshipStatus relStatus = (RelationshipStatus)parent.getItemAtPosition(position);
                relationshipStatus.setText(getRelationshipStatusLabel(relStatus));
                presenter.setRelationshipStatus(relStatus);
            }
        });

        purposeOfBeingAdapter = new PurposeOfBeingAdapter(this, this);
        purposesOfBeing.setAdapter(purposeOfBeingAdapter);

        if (presenter == null || savedInstanceState == null) {
            presenter = new EditUserPresenter(this);
        } else {
            presenter = new EditUserPresenter(savedInstanceState);
        }
        presenter.onTakeView(this);

    }

    public void onSaved() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    public void render(String name,
                       String dateOfBirth,
                       RelationshipStatus relStatus,
                       String description,
                       Set<PurposeOfBeing> purposesOfBeing) {

        progressBar.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        this.name.setText(name);
        this.dateOfBirth.setText(dateOfBirth);
        this.description.setText(description);
        if (relStatus != null) {
            relationshipStatus.setText(getRelationshipStatusLabel(relStatus));
        }
        if (purposesOfBeing != null) {
            this.purposeOfBeingAdapter.setCheckedPurposes(purposesOfBeing);
        }
    }

    public void render(String error) {
        progressBar.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        saveMenuItem.setActionView(null);
        ToastUtils.show(this, error);
    }

    @Override
    public void onChange(Set<PurposeOfBeing> purposes) {
        presenter.setPurposesOfBeing(purposes);
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
                presenter.save(name.getText().toString(),
                               dateOfBirth.getText().toString(),
                               description.getText().toString());
                return true;
        }
        return false;
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, EditUserActivity.class);
    }

    protected String getRelationshipStatusLabel(RelationshipStatus relationshipStatus) {
        return getResources().getString(getResources().getIdentifier("rs_" + relationshipStatus.name().toLowerCase(), "string", getPackageName()));
    }

    protected String getPurposeOfBeingLabel(PurposeOfBeing purpose) {
        return getResources().getString(getResources().getIdentifier("pob_" + purpose.name().toLowerCase(), "string", getPackageName()));
    }

}
