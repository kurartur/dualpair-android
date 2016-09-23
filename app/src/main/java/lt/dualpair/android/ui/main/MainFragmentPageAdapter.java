package lt.dualpair.android.ui.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;

import lt.dualpair.android.R;

public class MainFragmentPageAdapter extends FragmentStatePagerAdapter {

    Context context;

    public MainFragmentPageAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (Tab.fromPosition(position)) {
            case REVIEW:
                return new ReviewFragment();
            case MATCH_LIST:
                return new MatchListFragment();
            case PROFILE:
                return new ProfileFragment();
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public int getCount() {
        return Tab.values().length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (Tab.fromPosition(position)) {
            case REVIEW:
                return context.getResources().getString(R.string.tab_review);
            case MATCH_LIST:
                return context.getResources().getString(R.string.tab_pairs);
            case PROFILE:
                return context.getResources().getString(R.string.profile);
            default:
                throw new IllegalArgumentException();
        }
    }

    private enum Tab {
        REVIEW(0),
        MATCH_LIST(1),
        PROFILE(2);

        int position;

        Tab(int position) {
            this.position = position;
        }

        public static Tab fromPosition(int position) throws IllegalArgumentException {
            for (Tab tab : Tab.values()) {
                if (tab.position == position) {
                    return tab;
                }
            }
            throw new IllegalArgumentException("Unknown enum value: "+ position);
        }

        public int getPosition() {
            return position;
        }
    }
}
