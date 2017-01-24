package lt.dualpair.android.ui.socionics;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.BuildConfig;
import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.Choice;
import lt.dualpair.android.data.resource.ChoicePair;
import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.ui.user.ConfirmSociotypeActivity;
import lt.dualpair.android.utils.DrawableUtils;
import lt.dualpair.android.utils.ToastUtils;

public class SocionicsTestActivity extends BaseActivity {

    private static final int MENU_ITEM_SUBMIT = 1;
    private static final int MENU_ITEM_HELP = 2;
    private static final int MENU_ITEM_RANDOM = 3;

    private static final String TAG = "SocionicsTestActivity";

    @Bind(R.id.choices) RecyclerView choicesView;
    TextView leftPairsCounterText;

    private SocionicsTestRecyclerAdapter adapter;

    private static SocionicsTestPresenter presenter;

    private View defaultSubmitMenuItemView;
    private MenuItem submitMenuItem;

    private int selectedItems;
    private int totalItems;
    private boolean itemsAreSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.socionics_test_layout);
        setupActionBar(true, getString(R.string.socionics_test));

        ButterKnife.bind(this);

        if (presenter == null || savedInstanceState == null) {
            presenter = new SocionicsTestPresenter();
        } else {
            presenter = new SocionicsTestPresenter(savedInstanceState);
        }

        adapter = new SocionicsTestRecyclerAdapter(new ArrayList<ChoicePair>(), new SocionicsTestRecyclerAdapter.OnChoiceListener() {
            @Override
            public void onChoice(String id, int position, Choice choice) {
                presenter.onChoice(id, choice);
            }
        });
        choicesView.setAdapter(adapter);

        leftPairsCounterText = new TextView(this);
        leftPairsCounterText.setPadding(20, 20, 40, 20);

        presenter.onTakeView(this);

        showHelp();
    }

    public void setChoicePairs(List<ChoicePair> choicePairs) {
        if (!itemsAreSet) {
            adapter.setItems(choicePairs);
            adapter.notifyDataSetChanged();
            itemsAreSet = true;
        }

        selectedItems = countAlreadySelected(choicePairs);
        totalItems = choicePairs.size();
        leftPairsCounterText.setText(getString(R.string.socionics_test_counter, selectedItems, totalItems));
        setSubmitMenuItem();
    }

    private void setSubmitMenuItem() {
        if (submitMenuItem != null) {
            if (selectedItems == totalItems) {
                submitMenuItem.setActionView(defaultSubmitMenuItemView);
            } else {
                submitMenuItem.setActionView(leftPairsCounterText);
            }
        }
    }

    private int countAlreadySelected(List<ChoicePair> choicePairs) {
        int c = 0;
        for (ChoicePair choicePair : choicePairs) {
            if (choicePair.isAnySelected()) {
                c += 1;
            }
        }
        return c;
    }

    public void onSelectionChange(String id) {
        adapter.notifyItemChanged(id);
    }

    public void showResults(final Sociotype sociotype) {
        Intent intent = new Intent(this, ConfirmSociotypeActivity.class);
        intent.putExtra(ConfirmSociotypeActivity.PARAM_SOCIOTYPE, sociotype);
        startActivity(intent);
        finish();
    }

    public void showError(String error) {
        ToastUtils.show(this, error);
    }

    private void showHelp() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(getString(R.string.how_to))
                .setMessage(R.string.socionics_test_help)
                .setPositiveButton(getString(R.string.got_it), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        dialogBuilder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem helpMenuItem = menu.add(Menu.NONE, MENU_ITEM_HELP, Menu.NONE, R.string.help);
        helpMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        helpMenuItem.setIcon(DrawableUtils.getActionBarIcon(this, R.drawable.ic_help_black_48dp));
        submitMenuItem = menu.add(Menu.NONE, MENU_ITEM_SUBMIT, Menu.NONE, R.string.save);
        submitMenuItem.setIcon(DrawableUtils.getActionBarIcon(this, R.drawable.ic_done_black_48dp));
        submitMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        defaultSubmitMenuItemView = submitMenuItem.getActionView();
        setSubmitMenuItem();
        if (BuildConfig.DEBUG) {
            menu.add(Menu.NONE, MENU_ITEM_RANDOM, Menu.NONE, R.string.random);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) return true;
        switch (item.getItemId()) {
            case MENU_ITEM_SUBMIT:
                if (selectedItems == totalItems) {
                    submitMenuItem.setActionView(R.layout.action_progressbar);
                    presenter.submitTest(this);
                }
                return true;
            case MENU_ITEM_HELP:
                showHelp();
                return true;
            case MENU_ITEM_RANDOM:
                presenter.selectRandom();
                return true;
        }
        return false;

    }
}
