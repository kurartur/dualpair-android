package lt.dualpair.android.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import lt.dualpair.android.R;
import lt.dualpair.android.ui.BaseFragment;
import lt.dualpair.android.ui.CustomActionBarActivity;
import lt.dualpair.android.ui.CustomActionBarFragment;

public abstract class MainTabFragment extends BaseFragment implements CustomActionBarFragment {

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

    private void setupActionBar() {
        FragmentActivity activity = getActivity();
        if (activity != null && activity instanceof CustomActionBarActivity) {
            ((CustomActionBarActivity) activity).requestActionBar(this);
        }
    }

    public View getActionBarView() {
        return null;
    }

    public String getActionBarTitle() {
        return getResources().getString(R.string.app_name);
    }

}
