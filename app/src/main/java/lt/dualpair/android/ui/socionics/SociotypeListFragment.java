package lt.dualpair.android.ui.socionics;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.Sociotype;
import lt.dualpair.android.ui.BaseFragment;
import lt.dualpair.android.utils.ActionBarUtils;
import lt.dualpair.android.utils.DrawableUtils;

public class SociotypeListFragment extends BaseFragment {

    private static final String ARG_ENABLE_HOME = "enableHome";

    private static final int MENU_ITEM_TEST = 1;

    @Bind(R.id.sociotypes)
    RecyclerView sociotypesView;

    private SociotypeListRecyclerAdapter.OnSociotypeClickListener onSociotypeClickListener;

    private SociotypesViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.layout_sociotype_list, container, false);
        ButterKnife.bind(this, view);
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
        ActionBarUtils.setTitle(getActivity(), getString(R.string.sociotypes));
        ActionBarUtils.setHomeButtonEnabled(getActivity(), enableHome());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onSociotypeClickListener = (SociotypeListRecyclerAdapter.OnSociotypeClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnSociotypeClickListener");
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuItem saveMenuItem = menu.add(Menu.NONE, MENU_ITEM_TEST, Menu.NONE, R.string.socionics_test);
        saveMenuItem.setIcon(DrawableUtils.getActionBarIcon(getContext(), R.drawable.ic_test_black_36dp));
        saveMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case MENU_ITEM_TEST:
                SocionicsTestFragment fragment = SocionicsTestFragment.newInstance();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, fragment);
                ft.addToBackStack(null);
                ft.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean enableHome() {
        return getArguments().getBoolean(ARG_ENABLE_HOME);
    }

    private void subscribeUi() {
        viewModel.getSociotypes().observe(this, new Observer<List<Sociotype>>() {
            @Override
            public void onChanged(@Nullable List<Sociotype> sociotypes) {
                sociotypesView.setAdapter(new SociotypeListRecyclerAdapter(sociotypes, onSociotypeClickListener));
            }
        });
    }

    public static SociotypeListFragment newInstance(boolean enableHome) {
        SociotypeListFragment f = new SociotypeListFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_ENABLE_HOME, enableHome);
        f.setArguments(args);
        return f;
    }

}
