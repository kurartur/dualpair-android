package lt.dualpair.android.ui.user;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.UserPhoto;
import lt.dualpair.android.ui.BaseFragment;
import lt.dualpair.android.ui.accounts.AccountType;
import lt.dualpair.android.utils.ToastUtils;

public class AvailableProviderPhotosFragment extends BaseFragment {

    private static final String TAG = AvailableProviderPhotosFragment.class.getName();

    private AccountType accountType;
    private AvailablePhotosSheetDialog.OnPhotoSelectedListener onPhotoSelectedListener;

    @Bind(R.id.photos)
    protected RecyclerView photosView;

    @Bind(R.id.no_photos)
    protected View noPhotosView;

    private EditPhotosViewModel viewModel;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.available_provider_photos_layout, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity(), new EditPhotosViewModel.Factory(getActivity().getApplication())).get(EditPhotosViewModel.class);
        subscribeUi();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    private void subscribeUi() {
        Disposable disposable = viewModel.getAvailablePhotos(accountType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(photos -> {
                if (photos.size() > 0) {
                    photosView.setAdapter(new AvailableProviderPhotosRecyclerAdapter(photos, new AvailableProviderPhotosRecyclerAdapter.OnPhotoClickListener() {
                        @Override
                        public void onClick(UserPhoto photo) {
                            onPhotoSelectedListener.onPhotoSelected(photo);
                        }
                    }));
                } else {
                    noPhotosView.setVisibility(View.VISIBLE);
                }
            }, throwable -> {
                Log.e(TAG, throwable.getMessage(), throwable);
                ToastUtils.show(getContext(), throwable.getMessage());
            });
        this.disposable.add(disposable);
    }

    public static AvailableProviderPhotosFragment getInstance(AccountType accountType,
                                                              AvailablePhotosSheetDialog.OnPhotoSelectedListener onPhotoSelectedListener) {
        AvailableProviderPhotosFragment fragment = new AvailableProviderPhotosFragment();
        fragment.accountType = accountType;
        fragment.onPhotoSelectedListener = onPhotoSelectedListener;
        return fragment;
    }
}
