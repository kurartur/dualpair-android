package lt.dualpair.android.ui.accounts;

import lt.dualpair.android.R;

public enum AccountType {

    FB(R.drawable.fb_f_logo__blue_50),
    VK(R.drawable.vkontakte_logo),
    FK(R.drawable.fb_f_logo__blue_50);

    private int icon;

    AccountType(int icon) {
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }
}
