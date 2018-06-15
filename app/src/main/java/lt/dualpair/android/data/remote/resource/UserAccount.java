package lt.dualpair.android.data.remote.resource;

import java.io.Serializable;

import lt.dualpair.android.ui.accounts.AccountType;

public class UserAccount implements Serializable {

    private AccountType accountType;
    private String accountId;

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
