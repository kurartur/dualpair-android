package lt.dualpair.android.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.UserDataManager;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.utils.ToastUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class EditPhotosActivity extends BaseActivity {

    private static final String TAG = "EditPhotosActivity";

    @Bind(R.id.photos) RecyclerView photosView;

    private List<Photo> photos;
    private User user;
    private EditPhotosRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_photos_layout);

        setupActionBar(true, getResources().getString(R.string.photos));

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
                    public void onNext(User u) {
                        user = u;
                        photos = user.getPhotos();
                        renderGrid(photos);
                    }
                });
    }

    private void renderGrid(List<Photo> photos) {
        View.OnClickListener onAddClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AvailablePhotosSheetDialog dialog = AvailablePhotosSheetDialog.getInstance(user);
                dialog.setOnPhotoSelectedListener(new OnNewPhotoSelectedListener(EditPhotosActivity.this, dialog, adapter));
                dialog.show(getSupportFragmentManager(), "AvailablePhotosSheetDialog");
            }
        };
        adapter = new EditPhotosRecyclerAdapter(photos, onAddClickListener, new EditPhotosRecyclerAdapter.OnRemoveListener() {
            @Override
            public void onRemove(Photo photo) {
                new UserDataManager(EditPhotosActivity.this).deletePhoto(photo)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new EmptySubscriber<User>() {
                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "Unable to remove photo", e);
                                ToastUtils.show(EditPhotosActivity.this, "Unable to remove photo");
                            }
                        });
            }
        });
        photosView.setAdapter(adapter);
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, EditPhotosActivity.class);
    }

    private static class OnNewPhotoSelectedListener implements AvailablePhotosSheetDialog.OnPhotoSelectedListener {

        private EditPhotosActivity editPhotosActivity;
        private AvailablePhotosSheetDialog dialog;
        private EditPhotosRecyclerAdapter adapter;

        public OnNewPhotoSelectedListener(EditPhotosActivity editPhotosActivity, AvailablePhotosSheetDialog dialog, EditPhotosRecyclerAdapter adapter) {
            this.editPhotosActivity = editPhotosActivity;
            this.dialog = dialog;
            this.adapter = adapter;
        }

        @Override
        public void onPhotoSelected(Photo photo) {
            adapter.addPhoto(photo);
            new UserDataManager(editPhotosActivity).addPhoto(photo)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new EmptySubscriber<User>() {
                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "Unable to save photo", e);
                            ToastUtils.show(editPhotosActivity, "Unable to save photo");
                        }
                    });
            dialog.dismiss();
        }
    }

}
