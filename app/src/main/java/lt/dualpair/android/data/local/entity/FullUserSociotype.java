package lt.dualpair.android.data.local.entity;

public class FullUserSociotype {

    private UserSociotype userSociotype;
    private Sociotype sociotype;

    public FullUserSociotype(UserSociotype userSociotype, Sociotype sociotype) {
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
