package com.artur.dualpair.android.ui.main;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.artur.dualpair.android.R;
import com.artur.dualpair.android.core.user.GetUserPrincipalTask;
import com.artur.dualpair.android.core.user.SetLocationTask;
import com.artur.dualpair.android.dto.SearchParameters;
import com.artur.dualpair.android.dto.User;
import com.artur.dualpair.android.rx.EmptySubscriber;
import com.artur.dualpair.android.services.ServiceException;
import com.artur.dualpair.android.ui.BaseActivity;
import com.artur.dualpair.android.ui.search.SearchParametersActivity;
import com.artur.dualpair.android.ui.user.AddSociotypeActivity;
import com.artur.dualpair.android.ui.user.SetDateOfBirthActivity;
import com.artur.dualpair.android.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private User user;

    @Bind(R.id.tabs)
    TabLayout tabLayout;

    @Bind(R.id.viewpager)
    ViewPager viewPager;

    @Bind(R.id.frame_splash)
    FrameLayout splashFrame;

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

        setLocation();
        initUser();

        viewPager.setAdapter(new MainFragmentPageAdapter(this, getFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initUser() {
        splashFrame.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.GONE);
        new GetUserPrincipalTask(this).execute(new EmptySubscriber<User>() {
            @Override
            public void onError(Throwable e) {
                if (e instanceof ServiceException && ((ServiceException)e).getResponse().code() != 401) {
                    Log.e(TAG, "Unable to load user", e);
                    ToastUtils.show(MainActivity.this, "Unable to load user");
                }
                finish();
            }

            @Override
            public void onNext(User user) {
                checkAndSetUser(user);
                splashFrame.setVisibility(View.GONE);
                mainLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void checkAndSetUser(User user) {
        if (user.getSociotypes().isEmpty()) {
            openAddUserSociotype();
            return;
        }
        if (user.getDateOfBirth() == null) {
            openSetBirthday();
            return;
        }
        SearchParameters searchParameters = user.getSearchParameters();
        if (searchParameters == null
                || (!searchParameters.getSearchFemale() && !searchParameters.getSearchMale())
                || searchParameters.getMinAge() == null
                || searchParameters.getMaxAge() == null) {
            openSearchParameters();
            return;
        }

        this.user = user;
    }

    private void openAddUserSociotype() {
        Intent intent = new Intent(this, AddSociotypeActivity.class);
        startActivity(intent);
        finish();
    }

    private void openSetBirthday() {
        Intent intent = new Intent(this, SetDateOfBirthActivity.class);
        startActivity(intent);
        finish();
    }

    private void openSearchParameters() {
        Intent intent = new Intent(this, SearchParametersActivity.class);
        startActivity(intent);
        finish();
    }

    private void setLocation() {
        try {
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(lm.getBestProvider(new Criteria(), true));
            com.artur.dualpair.android.dto.Location locationDto = new com.artur.dualpair.android.dto.Location();
            locationDto.setLatitude(location.getLatitude());
            locationDto.setLongitude(location.getLongitude());
            new SetLocationTask(this, locationDto).execute(new EmptySubscriber<Void>() {
                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "Unable to set location", e);
                    ToastUtils.show(MainActivity.this, "Unable to set location");
                }

                @Override
                public void onNext(Void aVoid) {
                    super.onNext(aVoid);
                }
            });
        } catch (SecurityException se) {
            ToastUtils.show(this, "Cant get location manager");
            finish();
        }
    }
}
