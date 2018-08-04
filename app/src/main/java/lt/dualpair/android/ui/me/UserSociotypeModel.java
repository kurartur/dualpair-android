package lt.dualpair.android.ui.me;

import lt.dualpair.android.data.local.entity.Sociotype;
import lt.dualpair.android.data.local.entity.UserSociotype;

public class UserSociotypeModel {

    private UserSociotype userSociotype;
    private Sociotype sociotype;

    public UserSociotypeModel(UserSociotype userSociotype, Sociotype sociotype) {
        this.userSociotype = userSociotype;
        this.sociotype = sociotype;
    }

    public UserSociotype getUserSociotype() {
        return userSociotype;
    }

    public Sociotype getSociotype() {
        return sociotype;
    }
}
