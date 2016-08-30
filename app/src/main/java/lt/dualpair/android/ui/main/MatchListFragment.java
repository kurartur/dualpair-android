package lt.dualpair.android.ui.main;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.trello.rxlifecycle.ActivityLifecycleProvider;

import java.util.List;

import lt.dualpair.android.R;
import lt.dualpair.android.core.match.GetMatchListTask;
import lt.dualpair.android.resource.Match;
import lt.dualpair.android.rx.EmptySubscriber;

public class MatchListFragment extends Fragment {

    private static final String TAG = "MatchListFragment";

    private GridView gridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        gridView = (GridView)inflater.inflate(R.layout.layout_match_list, container, false);
        return gridView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeList();
    }

    private void initializeList() {
        final Activity activity = getActivity();
        new GetMatchListTask(activity).execute(new EmptySubscriber<List<Match>>() {
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Unable to load match list", e);
            }

            @Override
            public void onNext(List<Match> matches) {
                gridView.setAdapter(new MatchListAdapter(matches, activity));
            }
        }, (ActivityLifecycleProvider)activity);
    }

}
