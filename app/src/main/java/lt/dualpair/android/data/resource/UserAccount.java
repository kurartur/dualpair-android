package lt.dualpair.android.data.resource;

import java.io.Serializable;

public class UserAccount implements Serializable {

    private String accountType;
    private String accountId;

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
