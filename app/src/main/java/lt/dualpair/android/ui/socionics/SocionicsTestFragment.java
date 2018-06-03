package lt.dualpair.android.ui.socionics;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.BuildConfig;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.Choice;
import lt.dualpair.android.data.local.entity.ChoicePair;
import lt.dualpair.android.data.local.entity.Sociotype;
import lt.dualpair.android.ui.BaseFragment;
import lt.dualpair.android.utils.ActionBarUtils;
import lt.dualpair.android.utils.DrawableUtils;
import lt.dualpair.android.utils.ToastUtils;

public class SocionicsTestFragment extends BaseFragment {

    private static final String TAG = SocionicsTestFragment.class.getName();

    private static final int MENU_ITEM_SUBMIT = 1;
    private static final int MENU_ITEM_HELP = 2;
    private static final int MENU_ITEM_RANDOM = 3;

    @Bind(R.id.choices)
    RecyclerView choicesView;
    private TextView leftPairsCounterText;
    private View defaultSubmitMenuItemView;
    private MenuItem submitMenuItem;

    private SocionicsTestRecyclerAdapter adapter;

    private SociotypesViewModel viewModel;

    private int selectedItems;
    private int totalItems;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.socionics_test_layout, container, false);
        ButterKnife.bind(this, view);

        leftPairsCounterText = new TextView(getContext());
        leftPairsCounterText.setPadding(20, 20, 40, 20);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity(), new SociotypesViewModel.Factory(getActivity().getApplication())).get(SociotypesViewModel.class);
        subscribeUi();
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBarUtils.setTitle(getActivity(), getString(R.string.socionics_test));
        ActionBarUtils.setHomeButtonEnabled(getActivity(), true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem helpMenuItem = menu.add(Menu.NONE, MENU_ITEM_HELP, Menu.NONE, R.string.help);
        helpMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        helpMenuItem.setIcon(DrawableUtils.getActionBarIcon(getContext(), R.drawable.ic_help_black_48dp));
        submitMenuItem = menu.add(Menu.NONE, MENU_ITEM_SUBMIT, Menu.NONE, R.string.save);
        submitMenuItem.setIcon(DrawableUtils.getActionBarIcon(getContext(), R.drawable.ic_done_black_48dp));
        submitMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        defaultSubmitMenuItemView = submitMenuItem.getActionView();
        setSubmitMenuItem();
        if (BuildConfig.DEBUG) {
            menu.add(Menu.NONE, MENU_ITEM_RANDOM, Menu.NONE, R.string.random);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case MENU_ITEM_SUBMIT:
                if (selectedItems == totalItems) {
                    submitMenuItem.setActionView(R.layout.action_progressbar);
                    Disposable disposable = viewModel.evaluateTest()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Sociotype>() {
                            @Override
                            public void accept(Sociotype sociotype) throws Exception {
                                ConfirmableSociotypeDetailsFragment fragment = ConfirmableSociotypeDetailsFragment.newInstance(sociotype);
                                FragmentManager fragmentManager = getFragmentManager();
                                fragmentManager.popBackStack();
                                FragmentTransaction ft = fragmentManager.beginTransaction();
                                ft.replace(R.id.fragment_container, fragment);
                                ft.addToBackStack(null);
                                ft.commit();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                ToastUtils.show(getContext(), throwable.getMessage());
                            }
                        });
                    this.disposable.add(disposable);
                }
                return true;
            case MENU_ITEM_HELP:
                showHelp();
                return true;
            case MENU_ITEM_RANDOM:
                viewModel.fillRandomTestValues();
                return true;
        }
        return false;

    }

    private void subscribeUi() {
        viewModel.getChoicePairs().observe(this, new Observer<Map<String, ChoicePair>>() {
            @Override
            public void onChanged(@Nullable Map<String, ChoicePair> choicePairMap) {
                List<ChoicePair> choicePairList = new ArrayList<>(choicePairMap.values());
                SocionicsTestRecyclerAdapter adapter = new SocionicsTestRecyclerAdapter(choicePairList, new SocionicsTestRecyclerAdapter.OnChoiceListener() {
                    @Override
                    public void onChoice(String id, int position, Choice choice) {
                        viewModel.onChoice(id, choice);
                    }
                });
                choicesView.setAdapter(adapter);

                selectedItems = countAlreadySelected(choicePairList);
                totalItems = choicePairList.size();
                leftPairsCounterText.setText(getString(R.string.socionics_test_counter, selectedItems, totalItems));
                setSubmitMenuItem();

            }
        });
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

    private void setSubmitMenuItem() {
        if (submitMenuItem != null) {
            if (selectedItems == totalItems) {
                submitMenuItem.setActionView(defaultSubmitMenuItemView);
            } else {
                submitMenuItem.setActionView(leftPairsCounterText);
            }
        }
    }

    private void showHelp() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
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

    public static SocionicsTestFragment newInstance() {
        SocionicsTestFragment f = new SocionicsTestFragment();
        return f;
    }
}
