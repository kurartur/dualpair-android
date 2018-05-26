package lt.dualpair.android.data.local.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Relation;

import java.util.List;

public class History {

    private Long id;

    @ColumnInfo(name = "user_id")
    private Long userId;
    private String sourceLink;
    private String name;
    private String answer;
    private Long matchId;

    @Relation(parentColumn = "user_id", entityColumn = "user_id")
    private List<UserAccount> accounts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSourceLink() {
        return sourceLink;
    }

    public void setSourceLink(String sourceLink) {
        this.sourceLink = sourceLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<UserAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<UserAccount> accounts) {
        this.accounts = accounts;
    }

    public boolean isMatch() {
        return matchId != null;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Long getMatchId() {
        return matchId;
    }

    public UserAccount getAccountByType(String accountType) {
        if (getAccounts() != null) {
            for (UserAccount account : getAccounts()) {
                if (account.getAccountType().equals(accountType)) {
                    return account;
                }
            }
        }
        return null;
    }
}
