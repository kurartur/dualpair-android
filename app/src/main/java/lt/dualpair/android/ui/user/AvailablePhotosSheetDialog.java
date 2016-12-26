package lt.dualpair.android.ui.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.resource.User;

public class AvailablePhotosSheetDialog extends BottomSheetDialogFragment {

    private User user;
    private OnPhotoSelectedListener onPhotoSelectedListener;

    @Bind(R.id.tabs)
    protected TabLayout tabLayout;

    @Bind(R.id.viewpager)
    protected ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = View.inflate(getContext(), R.layout.available_photos_sheet_layout, null);

        ButterKnife.bind(this, contentView);

        AvailablePhotosFragmentPageAdapter adapter = new AvailablePhotosFragmentPageAdapter(getChildFragmentManager(),
                user.getAccounts(),
                onPhotoSelectedListener);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (adapter.getIconId(i) == null) {
                tab.setText(adapter.getTitle(i));
            } else {
                tab.setIcon(adapter.getIconId(i));
            }
        }

        return contentView;
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }

    };

    public void setOnPhotoSelectedListener(OnPhotoSelectedListener onPhotoSelectedListener) {
        this.onPhotoSelectedListener = onPhotoSelectedListener;
    }

    public static AvailablePhotosSheetDialog getInstance(User user) {
        AvailablePhotosSheetDialog dialog = new AvailablePhotosSheetDialog();
        dialog.user = user;
        return dialog;
    }

    public interface OnPhotoSelectedListener {
        void onPhotoSelected(Photo photo);
    }

}
