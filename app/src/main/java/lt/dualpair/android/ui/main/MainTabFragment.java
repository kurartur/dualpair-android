package lt.dualpair.android.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import lt.dualpair.android.ui.BaseFragment;

public abstract class MainTabFragment extends BaseFragment {

    private boolean isVisibleToUser;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            setupActionBar();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isVisibleToUser) {
            setupActionBar();
        }
    }

    protected void setupActionBar() {
        if (getActivity() instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if (actionBar != null) {
                if (getActionBarView() != null) {
                    actionBar.setDisplayShowCustomEnabled(true);
                    actionBar.setDisplayShowTitleEnabled(false);
                    actionBar.setCustomView(getActionBarView());
                } else {
                    actionBar.setDisplayShowCustomEnabled(false);
                    actionBar.setDisplayShowTitleEnabled(true);
                }
            }
        }
    }

    protected View getActionBarView() {
        return null;
    }

}
