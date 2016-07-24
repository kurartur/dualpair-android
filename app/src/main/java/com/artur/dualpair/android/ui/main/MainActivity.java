package com.artur.dualpair.android.ui.main;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;

import com.artur.dualpair.android.R;
import com.artur.dualpair.android.ui.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    @Bind(R.id.tabs)
    TabLayout tabLayout;

    @Bind(R.id.viewpager)
    ViewPager viewPager;

    @Bind(R.id.layout_main)
    LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        ButterKnife.bind(this);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        viewPager.setAdapter(new MainFragmentPageAdapter(this, getFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, MainActivity.class);
    }
}
