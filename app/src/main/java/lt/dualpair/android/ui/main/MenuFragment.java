package lt.dualpair.android.ui.main;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.ui.search.SearchParametersActivity;

public class MenuFragment extends Fragment {

    private static final int SEARCH_PARAMETERS_REQUEST_CODE = 1;

    @Bind(R.id.search_parameters_layout)
    LinearLayout searchParametersLayout;

    @Bind(R.id.logout_layout)
    LinearLayout logoutLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_menu, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Activity activity = getActivity();
        searchParametersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(SearchParametersActivity.createIntent(activity), SEARCH_PARAMETERS_REQUEST_CODE);
            }
        });
    }
}
