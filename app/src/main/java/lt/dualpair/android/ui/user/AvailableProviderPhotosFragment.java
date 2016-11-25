package lt.dualpair.android.ui.user;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.task.user.GetAvailablePhotosTask;
import lt.dualpair.android.ui.BaseFragment;
import lt.dualpair.android.ui.accounts.AccountType;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        new GetAvailablePhotosTask(null, accountType).execute(getActivity()) // TODO auth token shouldn't be null
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptySubscriber<List<Photo>>() {
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Unable to load available photos", e);
            }

            @Override
            public void onNext(List<Photo> photos) {
                if (photos.size() > 0) {
                    photosView.setAdapter(new AvailableProviderPhotosRecyclerAdapter(photos, new AvailableProviderPhotosRecyclerAdapter.OnPhotoClickListener() {
                        @Override
                        public void onClick(Photo photo) {
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
