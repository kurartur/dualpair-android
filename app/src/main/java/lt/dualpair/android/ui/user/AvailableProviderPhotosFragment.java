package lt.dualpair.android.ui.user;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.R;
import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.local.entity.UserPhoto;
import lt.dualpair.android.data.remote.client.user.GetAvailablePhotosClient;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.ui.BaseFragment;
import lt.dualpair.android.ui.accounts.AccountType;

public class AvailableProviderPhotosFragment extends BaseFragment {

    private static final String TAG = "AvailProvPhotosFrag";

    private AccountType accountType;
    private AvailablePhotosSheetDialog.OnPhotoSelectedListener onPhotoSelectedListener;

    @Bind(R.id.photos)
    protected RecyclerView photosView;

    @Bind(R.id.no_photos)
    protected View noPhotosView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.available_provider_photos_layout, null);
        ButterKnife.bind(this, view);
        // TODO move to repository
        new GetAvailablePhotosClient(AccountUtils.getUserId(getContext()), accountType).observable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Photo>>() {
                    @Override
                    public void accept(List<Photo> photos) {
                        if (photos.size() > 0) {
                            List<UserPhoto> userPhotos = new ArrayList<>();
                            for (Photo photo : photos) {
                                UserPhoto userPhoto = new UserPhoto();
                                userPhoto.setSourceLink(photo.getSourceUrl());
                                userPhotos.add(userPhoto);
                            }
                            photosView.setAdapter(new AvailableProviderPhotosRecyclerAdapter(userPhotos, new AvailableProviderPhotosRecyclerAdapter.OnPhotoClickListener() {
                                @Override
                                public void onClick(UserPhoto photo) {
                                    onPhotoSelectedListener.onPhotoSelected(photo);
                                }
                            }));
                        } else {
                            noPhotosView.setVisibility(View.VISIBLE);
                        }
                    }
                });
        return view;
    }

    public static AvailableProviderPhotosFragment getInstance(AccountType accountType,
                                                              AvailablePhotosSheetDialog.OnPhotoSelectedListener onPhotoSelectedListener) {
        AvailableProviderPhotosFragment fragment = new AvailableProviderPhotosFragment();
        fragment.accountType = accountType;
        fragment.onPhotoSelectedListener = onPhotoSelectedListener;
        return fragment;
    }
}
