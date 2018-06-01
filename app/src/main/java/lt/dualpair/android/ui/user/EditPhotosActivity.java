package lt.dualpair.android.ui.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.UserPhoto;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.ui.user.EditPhotosViewModel.PhotoEditingData;
import lt.dualpair.android.utils.DrawableUtils;


public class EditPhotosActivity extends BaseActivity implements EditPhotosRecyclerAdapter.OnStartDragListener,
                                                                AvailablePhotosSheetDialog.OnPhotoSelectedListener {

    private static final String TAG = EditPhotosActivity.class.getName();

    private static final int MENU_ITEM_SAVE = 1;
    private static final int MENU_ITEM_HELP = 2;

    public static final String PHOTOS_KEY = "PHOTOS";
    public static final String RESULT_BUNDLE_KEY = "RESULT_BUNDLE";

    @Bind(R.id.photos) RecyclerView photosView;

    private EditPhotosRecyclerAdapter adapter;

    private ItemTouchHelper itemTouchHelper;

    private MenuItem menuItemSave;

    private AvailablePhotosSheetDialog dialog;

    private EditPhotosViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_photos_layout);

        setupActionBar(true, getResources().getString(R.string.photos));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(DrawableUtils.getActionBarIcon(this, R.drawable.ic_close_black_30dp));
        }

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

        viewModel = ViewModelProviders.of(this, new EditPhotosViewModel.Factory(getApplication())).get(EditPhotosViewModel.class);
        subscribeUi();
    }

    private void subscribeUi() {
        viewModel.getData().observe(this, new Observer<PhotoEditingData>() {
            @Override
            public void onChanged(@Nullable PhotoEditingData data) {
                render(data);
            }
        });
    }

    @Override
    public void onPhotoSelected(UserPhoto photo) {
        photo.setPosition(adapter.getItemCount() - 1);
        adapter.addPhoto(photo);
        dialog.dismiss();
    }

    public void render(final PhotoEditingData data) {

        adapter = new EditPhotosRecyclerAdapter(data.getPhotos(), new EditPhotosRecyclerAdapter.OnAddClickListener() {
            @Override
            public void onAddClick() {
                dialog = AvailablePhotosSheetDialog.getInstance(data.getUserAccounts());
                dialog.setOnPhotoSelectedListener(EditPhotosActivity.this);
                dialog.show(getSupportFragmentManager(), "AvailablePhotosSheetDialog");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItemHelp = menu.add(Menu.NONE, MENU_ITEM_HELP, Menu.NONE, R.string.help);
        menuItemHelp.setIcon(DrawableUtils.getActionBarIcon(this, R.drawable.ic_help_black_48dp));
        menuItemHelp.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItemSave = menu.add(Menu.NONE, MENU_ITEM_SAVE, Menu.NONE, R.string.save);
        menuItemSave.setIcon(DrawableUtils.getActionBarIcon(this, R.drawable.ic_done_black_48dp));
        menuItemSave.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case MENU_ITEM_SAVE:
                menuItemSave.setActionView(R.layout.action_progressbar);
                save();
                return true;
            case MENU_ITEM_HELP:
                showHelp();
                return true;
        }
        return false;
    }

    private void showHelp() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(getString(R.string.how_to))
                .setMessage(R.string.photo_edit_help)
                .setPositiveButton(getString(R.string.got_it), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        dialogBuilder.create().show();
    }

    private void save() {
        viewModel.save(adapter.getPhotos())
                .subscribe(() -> onSaved());
    }

    public void onSaved() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        save();
        super.onBackPressed();
    }
}
