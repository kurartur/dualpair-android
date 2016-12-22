package lt.dualpair.android.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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


public class EditPhotosActivity extends BaseActivity implements EditPhotosRecyclerAdapter.OnStartDragListener {

    private static final String TAG = "EditPhotosActivity";
    private static final int MENU_ITEM_DELETE = 1;
    private static final int MENU_ITEM_MOVE = 2;

    @Bind(R.id.photos) RecyclerView photosView;

    private EditPhotosRecyclerAdapter adapter;

    private ItemTouchHelper itemTouchHelper;

    private MenuItem menuItemMove;
    private MenuItem menuItemDelete;

    private static EditPhotosPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_photos_layout);

        setupActionBar(true, getResources().getString(R.string.photos));

        ButterKnife.bind(this);

        photosView.setLayoutManager(new SpannedGridLayoutManager(
                new SpannedGridLayoutManager.GridSpanLookup() {
                    @Override
                    public SpannedGridLayoutManager.SpanInfo getSpanInfo(int position) {
                        if (position == 0) {
                            return new SpannedGridLayoutManager.SpanInfo(2, 2);
                        } else {
                            return new SpannedGridLayoutManager.SpanInfo(1, 1);
                        }
                    }
                },
                3  /*Three columns*/ ,
                1f  /*We want our items to be 1:1 ratio*/ ));

        presenter = new EditPhotosPresenter(this);
        presenter.onTakeView(this);
    }

    public void render(final User user) {

        View.OnClickListener onAddClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AvailablePhotosSheetDialog dialog = AvailablePhotosSheetDialog.getInstance(user);
                dialog.setOnPhotoSelectedListener(new OnNewPhotoSelectedListener(EditPhotosActivity.this, dialog, adapter));
                dialog.show(getSupportFragmentManager(), "AvailablePhotosSheetDialog");
            }
        };
        adapter = new EditPhotosRecyclerAdapter(user.getPhotos(), onAddClickListener, new EditPhotosRecyclerAdapter.OnRemoveListener() {
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
            }, this);
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(photosView);
        photosView.setAdapter(adapter);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
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
            photo.setPosition(adapter.getItemCount());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuItemMove = menu.add(Menu.NONE, MENU_ITEM_MOVE, Menu.NONE, R.string.move);
        menuItemMove.setIcon(R.drawable.move);
        menuItemMove.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItemDelete = menu.add(Menu.NONE, MENU_ITEM_DELETE, Menu.NONE, R.string.delete);
        menuItemDelete.setIcon(R.drawable.trash);
        menuItemDelete.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }
        int currentMode = adapter.getMode();
        int selectedItemMode;
        switch (item.getItemId()) {
            case MENU_ITEM_MOVE:
                selectedItemMode = EditPhotosRecyclerAdapter.MOVE_MODE;
                break;
            case MENU_ITEM_DELETE:
                selectedItemMode = EditPhotosRecyclerAdapter.DELETE_MODE;
                break;
            default:
                throw new RuntimeException("Unrecognized menu item");
        }
        menuItemMove.getIcon().clearColorFilter();
        menuItemDelete.getIcon().clearColorFilter();
        if (currentMode == selectedItemMode) {
            adapter.setMode(EditPhotosRecyclerAdapter.NORMAL_MODE);
        } else {
            adapter.setMode(selectedItemMode);
            switch (selectedItemMode) {
                case EditPhotosRecyclerAdapter.MOVE_MODE:
                    menuItemMove.getIcon().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.MULTIPLY);
                    break;
                case EditPhotosRecyclerAdapter.DELETE_MODE:
                    menuItemDelete.getIcon().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.MULTIPLY);
                    break;
            }
        }
        return true;
    }
}
