package lt.dualpair.android.ui.user;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.data.local.entity.UserPhoto;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.utils.DrawableUtils;


public class EditPhotosActivity extends BaseActivity implements AvailablePhotosSheetDialog.OnPhotoSelectedListener {

    private static final String TAG = EditPhotosActivity.class.getName();

    private static final int MENU_ITEM_SAVE = 1;

    @Bind(R.id.photos) RecyclerView photosView;

    private EditPhotosRecyclerAdapter adapter;

    private boolean isSaving = false;

    private AvailablePhotosSheetDialog dialog;

    private CompositeDisposable disposable = new CompositeDisposable();

    private EditPhotosViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_photos_layout);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.photos);
        }

        ButterKnife.bind(this);

        photosView.setLayoutManager(new SpannedGridLayoutManager(
                new SpannedGridLayoutManager.GridSpanLookup() {
                    @Override
                    public SpannedGridLayoutManager.SpanInfo getSpanInfo(int position) {
                        if (position == 0) {
                            return new SpannedGridLayoutManager.SpanInfo(2, 2);
                        } else {
                            return SpannedGridLayoutManager.SpanInfo.SINGLE_CELL;
                        }
                    }
                },
                3  /*Three columns*/ ,
                1f  /*We want our items to be 1:1 ratio*/ ));

        viewModel = ViewModelProviders.of(this).get(EditPhotosViewModel.class);
        subscribeUi();
    }

    private void subscribeUi() {
        Disposable d = Single.zip(
                viewModel.getPhotos(),
                viewModel.getUserAccounts(),
                ZipResultHolder::new
            ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::render);
        disposable.add(d);
    }

    @Override
    public void onPhotoSelected(UserPhoto photo) {
        adapter.addPhoto(photo);
        dialog.dismiss();
    }

    public void render(final ZipResultHolder data) {
        adapter = new EditPhotosRecyclerAdapter(data.getPhotos(), () -> {
            dialog = AvailablePhotosSheetDialog.getInstance(data.getUserAccounts(), EditPhotosActivity.this);
            dialog.show(getSupportFragmentManager(), "AvailablePhotosSheetDialog");
        });
        photosView.setAdapter(adapter);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (!isSaving) {
            MenuItem menuItemSave = menu.add(Menu.NONE, MENU_ITEM_SAVE, Menu.NONE, R.string.save);
            menuItemSave.setIcon(DrawableUtils.getActionBarIcon(this, R.drawable.ic_done_black_48dp));
            menuItemSave.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        } else {
            MenuItem saving = menu.add(Menu.NONE, 0, Menu.NONE, "");
            saving.setActionView(R.layout.action_progressbar);
            saving.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_SAVE:
                save();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    private void save() {
        isSaving = true;
        invalidateOptionsMenu();
        Disposable d = viewModel.save(adapter.getPhotos())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {
                    setResult(Activity.RESULT_OK);
                    finish();
                    isSaving = false;
                });
        disposable.add(d);
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, EditPhotosActivity.class);
    }

    private static class ZipResultHolder {

        private List<UserPhoto> photos;
        private List<UserAccount> userAccounts;

        public ZipResultHolder(List<UserPhoto> photos, List<UserAccount> userAccounts) {
            this.photos = photos;
            this.userAccounts = userAccounts;
        }

        public List<UserPhoto> getPhotos() {
            return photos;
        }

        public List<UserAccount> getUserAccounts() {
            return userAccounts;
        }
    }

}
