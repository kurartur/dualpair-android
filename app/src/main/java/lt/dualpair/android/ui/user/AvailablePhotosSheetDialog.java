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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.data.local.entity.UserPhoto;

public class AvailablePhotosSheetDialog extends BottomSheetDialogFragment {

    private List<UserAccount> accounts;
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
                accounts,
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

    public static AvailablePhotosSheetDialog getInstance(List<UserAccount> accounts, AvailablePhotosSheetDialog.OnPhotoSelectedListener listener) {
        AvailablePhotosSheetDialog dialog = new AvailablePhotosSheetDialog();
        dialog.accounts = accounts;
        dialog.onPhotoSelectedListener = listener;
        return dialog;
    }

    public interface OnPhotoSelectedListener {
        void onPhotoSelected(UserPhoto photo);
    }

}
