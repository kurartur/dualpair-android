package lt.dualpair.android.data.local.entity;

import java.util.List;

public class UserForView {

    private User user;
    private List<UserPhoto> photos;
    private List<FullUserSociotype> sociotypes;
    private List<UserPurposeOfBeing> purposesOfBeing;
    private UserLocation lastLocation;
    private List<UserAccount> accounts;
    private Match match;
    private UserResponse userResponse;

    public UserForView() {}

    public UserForView(User user,
                       List<UserPhoto> photos,
                       List<FullUserSociotype> sociotypes,
                       List<UserPurposeOfBeing> purposesOfBeing,
                       UserLocation lastLocation,
                       List<UserAccount> accounts,
                       Match match,
                       UserResponse userResponse) {
        this.user = user;
        this.photos = photos;
        this.sociotypes = sociotypes;
        this.purposesOfBeing = purposesOfBeing;
        this.lastLocation = lastLocation;
        this.accounts = accounts;
        this.match = match;
        this.userResponse = userResponse;
    }

    public User getUser() {
        return user;
    }

    public List<UserPhoto> getPhotos() {
        return photos;
    }

    public List<FullUserSociotype> getSociotypes() {
        return sociotypes;
    }

    public List<UserPurposeOfBeing> getPurposesOfBeing() {
        return purposesOfBeing;
    }

    public UserLocation getLastLocation() {
        return lastLocation;
    }

    public List<UserAccount> getAccounts() {
        return accounts;
    }

    public Match getMatch() {
        return match;
    }

    public UserResponse getUserResponse() {
        return userResponse;
    }
}
