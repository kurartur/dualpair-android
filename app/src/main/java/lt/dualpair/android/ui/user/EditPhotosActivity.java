package lt.dualpair.android.ui.user;

import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.UserDataManager;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.ui.BaseActivity;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class EditPhotosActivity extends BaseActivity {

    private static final String TAG = "EditPhotosActivity";

    @Bind(R.id.photos) RecyclerView photosView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_photos_layout);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.photos));
            actionBar.setIcon(
                    new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        }

        ButterKnife.bind(this);

        load();
    }

    private void load() {
        new UserDataManager(this).getUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .compose(this.<User>bindToLifecycle())
                .subscribe(new EmptySubscriber<User>() {
                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Unable to load user", e);
                    }

                    @Override
                    public void onNext(User user) {
                        renderGrid(user.getPhotos());
                    }
                });
    }

    private void renderGrid(List<Photo> photos) {
        photosView.setAdapter(new EditPhotosRecyclerAdapter(photos));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

}
