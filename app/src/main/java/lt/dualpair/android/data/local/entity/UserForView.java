package lt.dualpair.android.data.local.entity;

import java.util.List;

public class UserForView {

    // TODO temporary field that will hold match id, matches shouldn't be saved
    private Long reference;
    private User user;
    private List<UserPhoto> photos;
    private List<FullUserSociotype> sociotypes;
    private List<UserPurposeOfBeing> purposesOfBeing;
    private UserLocation lastLocation;
    private Long matchId;
    private List<UserAccount> accounts;

    public UserForView() {}

    public UserForView(Long reference,
                       User user,
                       List<UserPhoto> photos,
                       List<FullUserSociotype> sociotypes,
                       List<UserPurposeOfBeing> purposesOfBeing,
                       UserLocation lastLocation,
                       Long matchId,
                       List<UserAccount> accounts) {
        this.reference = reference;
        this.user = user;
        this.photos = photos;
        this.sociotypes = sociotypes;
        this.purposesOfBeing = purposesOfBeing;
        this.lastLocation = lastLocation;
        this.matchId = matchId;
        this.accounts = accounts;
    }

    public Long getReference() {
        return reference;
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

    public Long getMatchId() {
        return matchId;
    }

    public boolean isMatched() {
        return matchId != null;
    }

    public List<UserAccount> getAccounts() {
        return accounts;
    }
}
