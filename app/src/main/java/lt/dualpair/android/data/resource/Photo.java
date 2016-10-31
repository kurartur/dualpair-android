package lt.dualpair.android.data.resource;

import java.io.Serializable;

import lt.dualpair.android.ui.accounts.AccountType;

public class Photo implements Serializable {

    private Long id;
    private AccountType accountType;
    private String idOnAccount;
    private String sourceUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getIdOnAccount() {
        return idOnAccount;
    }

    public void setIdOnAccount(String idOnAccount) {
        this.idOnAccount = idOnAccount;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}
