package lt.dualpair.android.ui.user;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.ui.accounts.AccountType;

public class AvailablePhotosFragmentPageAdapter extends FragmentPagerAdapter {

    private List<UserAccount> userAccounts;
    private AvailablePhotosSheetDialog.OnPhotoSelectedListener onPhotoSelectedListener;

    public AvailablePhotosFragmentPageAdapter(FragmentManager fm, List<UserAccount> userAccounts, AvailablePhotosSheetDialog.OnPhotoSelectedListener onPhotoSelectedListener) {
        super(fm);
        this.userAccounts = userAccounts;
        this.onPhotoSelectedListener = onPhotoSelectedListener;
    }

    @Override
    public Fragment getItem(int position) {
        return AvailableProviderPhotosFragment.getInstance(AccountType.valueOf(userAccounts.get(position).getAccountType()), onPhotoSelectedListener);
    }

    @Override
    public int getCount() {
        return userAccounts.size();
    }

    public Integer getIconId(int position) {
        return AccountType.valueOf(userAccounts.get(position).getAccountType()).getIcon();
    }

    public String getTitle(int position) {
        return userAccounts.get(position).getAccountType();
    }
}
