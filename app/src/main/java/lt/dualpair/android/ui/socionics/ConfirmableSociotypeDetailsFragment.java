package lt.dualpair.android.ui.socionics;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.Sociotype;
import lt.dualpair.android.utils.DrawableUtils;
import lt.dualpair.android.utils.ToastUtils;

public class ConfirmableSociotypeDetailsFragment extends SociotypeDetailsFragment {

    private static final int MENU_ITEM_SAVE = 1;

    private CompositeDisposable disposable = new CompositeDisposable();

    private boolean isSaving = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (!isSaving) {
            MenuItem saveMenuItem = menu.add(Menu.NONE, MENU_ITEM_SAVE, Menu.NONE, R.string.save);
            saveMenuItem.setIcon(DrawableUtils.getActionBarIcon(getContext(), R.drawable.ic_done_black_48dp));
            saveMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        } else {
            MenuItem saving = menu.add(Menu.NONE, 0, Menu.NONE, "");
            saving.setActionView(R.layout.action_progressbar);
            saving.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_SAVE:
                isSaving = true;
                getActivity().invalidateOptionsMenu();
                Disposable disposable = viewModel.saveSociotype(getSociotype().getCode1())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();
                            isSaving = false;
                        }, e -> {
                            ToastUtils.show(getContext(), e.getMessage());
                            isSaving = false;
                        });
                this.disposable.add(disposable);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    public static ConfirmableSociotypeDetailsFragment newInstance(Sociotype sociotype) {
        ConfirmableSociotypeDetailsFragment f = new ConfirmableSociotypeDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SOCIOTYPE, sociotype);
        f.setArguments(args);
        return f;
    }

    public interface OnSaveClickListener {
        void onSave(Sociotype sociotype);
    }
}
