package lt.dualpair.android.data.local.entity;

import java.util.List;

public class UserListItem {

    private User user;
    private UserPhoto photo;
    private List<UserAccount> accounts;
    private Long reference;

    private Swipe swipe;
    private Match match;

    public UserListItem(Long reference, User user, List<UserAccount> accounts, UserPhoto photo) {
        this.reference = reference;
        this.user = user;
        this.accounts = accounts;
        this.photo = photo;
    }

    public Long getUserId() {
        return user.getId();
    }

    public String getName() {
        return user.getName();
    }

    public UserPhoto getPhoto() {
        return photo;
    }

    public UserAccount getAccountByType(String accountType) {
        if (accounts != null) {
            for (UserAccount account : accounts) {
                if (account.getAccountType().equals(accountType)) {
                    return account;
                }
            }
        }
        return null;
    }

    public Long getReference() {
        return reference;
    }

    public Swipe getSwipe() {
        return swipe;
    }

    public Match getMatch() {
        return match;
    }
}
