package lt.dualpair.android.ui.accounts;

import lt.dualpair.android.data.local.entity.UserAccount;

public class SocialAccountItem {

    private AccountType accountType;
    private UserAccount userAccount;

    public SocialAccountItem(AccountType accountType, UserAccount userAccount) {
        this.accountType = accountType;
        this.userAccount = userAccount;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }
}
