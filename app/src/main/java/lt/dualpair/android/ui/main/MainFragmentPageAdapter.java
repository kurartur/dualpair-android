package lt.dualpair.android.ui.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;

import lt.dualpair.android.R;

public class MainFragmentPageAdapter extends FragmentStatePagerAdapter {

    private Context context;

    public MainFragmentPageAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return Tab.fromPosition(position).getFragmentCreator().create();
    }

    @Override
    public int getCount() {
        return Tab.values().length;
    }

    public Integer getIconId(int position) {
        return Tab.fromPosition(position).getIcon();
    }

    public Integer getTitleId(int position) {
        return Tab.fromPosition(position).getTitle();
    }

    private enum Tab {
        REVIEW(0, null, R.string.tab_review, new FragmentCreator() {
            @Override
            public Fragment create() {
                return new ReviewFragment();
            }
        }),
        MATCH_LIST(1, null, R.string.tab_pairs, new FragmentCreator() {
            @Override
            public Fragment create() {
                return new MutualMatchListFragment();
            }
        }),
        PROFILE(2, R.drawable.profile_icon, R.string.profile, new FragmentCreator() {
            @Override
            public Fragment create() {
                return new ProfileFragment();
            }
        });

        int position;
        Integer icon;
        Integer title;
        FragmentCreator fragmentCreator;

        Tab(int position, Integer icon, Integer title, FragmentCreator fragmentCreator) {
            this.position = position;
            this.icon = icon;
            this.title = title;
            this.fragmentCreator = fragmentCreator;
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

        public Integer getIcon() {
            return icon;
        }

        public Integer getTitle() {
            return title;
        }

        public FragmentCreator getFragmentCreator() {
            return fragmentCreator;
        }
    }

    private interface FragmentCreator {
        Fragment create();
    }
}
