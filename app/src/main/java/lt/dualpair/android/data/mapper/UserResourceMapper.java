package lt.dualpair.android.data.mapper;

import java.util.ArrayList;
import java.util.List;

import lt.dualpair.android.data.local.dao.SociotypeDao;
import lt.dualpair.android.data.local.entity.PurposeOfBeing;
import lt.dualpair.android.data.local.entity.RelationshipStatus;
import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.data.local.entity.UserLocation;
import lt.dualpair.android.data.local.entity.UserPhoto;
import lt.dualpair.android.data.local.entity.UserPurposeOfBeing;
import lt.dualpair.android.data.local.entity.UserSociotype;
import lt.dualpair.android.data.remote.resource.Location;
import lt.dualpair.android.data.remote.resource.PhotoResource;
import lt.dualpair.android.data.remote.resource.Sociotype;
import lt.dualpair.android.data.remote.resource.User;

public class UserResourceMapper {

    private SociotypeDao sociotypeDao;

    public UserResourceMapper(SociotypeDao sociotypeDao) {
        this.sociotypeDao = sociotypeDao;
    }

    public Result map(User userResource) {
        lt.dualpair.android.data.local.entity.User user = mapUser(new lt.dualpair.android.data.local.entity.User(), userResource);
        List<UserAccount> userAccounts = new ArrayList<>();
        Long userId = user.getId();
        if (userResource.getAccounts() != null) {
            for (lt.dualpair.android.data.remote.resource.UserAccount account : userResource.getAccounts()) {
                UserAccount userAccount = new UserAccount();
                userAccount.setUserId(userId);
                userAccount.setAccountId(account.getAccountId());
                userAccount.setAccountType(account.getAccountType().name());
                userAccounts.add(userAccount);
            }
        }
        List<UserPhoto> userPhotos = new ArrayList<>();
        for (PhotoResource photo : userResource.getPhotos()) {
            UserPhoto userPhoto = new UserPhoto();
            userPhoto.setId(photo.getId());
            userPhoto.setUserId(userId);
            userPhoto.setPosition(photo.getPosition());
            userPhoto.setSourceLink(photo.getSource());
            userPhotos.add(userPhoto);
        }
        List<UserSociotype> userSociotypes = new ArrayList<>();
        if (userResource.getSociotypes() != null) {
            for (Sociotype sociotype : userResource.getSociotypes()) {
                UserSociotype userSociotype = new UserSociotype();
                userSociotype.setUserId(userId);
                userSociotype.setSociotypeId(sociotypeDao.getSociotype(lt.dualpair.android.data.local.entity.Sociotype.Code.valueOf(sociotype.getCode1())).getId());
                userSociotypes.add(userSociotype);
            }
        }
        List<UserPurposeOfBeing> userPurposesOfBeing = new ArrayList<>();
        if (userResource.getPurposesOfBeing() != null) {
            for (String purposeOfBeing : userResource.getPurposesOfBeing()) {
                UserPurposeOfBeing userPurposeOfBeing = new UserPurposeOfBeing();
                userPurposeOfBeing.setUserId(userId);
                userPurposeOfBeing.setPurpose(PurposeOfBeing.fromCode(purposeOfBeing));
                userPurposesOfBeing.add(userPurposeOfBeing);
            }
        }
        List<UserLocation> userLocations = new ArrayList<>();
        if (userResource.getLocations() != null) {
            for (Location locationResource : userResource.getLocations()) {
                UserLocation userLocation = new UserLocation();
                userLocation.setUserId(userId);
                userLocation.setLatitude(locationResource.getLatitude());
                userLocation.setLongitude(locationResource.getLongitude());
                userLocation.setCity(locationResource.getCity());
                userLocation.setCountryCode(locationResource.getCountryCode());
                userLocations.add(userLocation);
            }
        }
        return new Result(user, userAccounts, userPhotos, userSociotypes, userPurposesOfBeing, userLocations);
    }

    private lt.dualpair.android.data.local.entity.User mapUser(lt.dualpair.android.data.local.entity.User to, lt.dualpair.android.data.remote.resource.User from) {
        to.setId(from.getId());
        to.setName(from.getName());
        to.setDateOfBirth(from.getDateOfBirth());
        to.setAge(from.getAge());
        to.setDescription(from.getDescription());
        to.setRelationshipStatus(RelationshipStatus.fromCode(from.getRelationshipStatus()));
        to.setGender(from.getGender());
        return to;
    }

    public static class Result {
        private lt.dualpair.android.data.local.entity.User user;
        private List<UserAccount> userAccounts;
        private List<UserPhoto> userPhotos;
        private List<UserSociotype> userSociotypes;
        private List<UserPurposeOfBeing> userPurposesOfBeing;
        private List<UserLocation> userLocations;

        public Result(lt.dualpair.android.data.local.entity.User user,
                      List<UserAccount> userAccounts,
                      List<UserPhoto> userPhotos,
                      List<UserSociotype> userSociotypes,
                      List<UserPurposeOfBeing> userPurposesOfBeing,
                      List<UserLocation> userLocations) {
            this.user = user;
            this.userAccounts = userAccounts;
            this.userPhotos = userPhotos;
            this.userSociotypes = userSociotypes;
            this.userPurposesOfBeing = userPurposesOfBeing;
            this.userLocations = userLocations;
        }

        public lt.dualpair.android.data.local.entity.User getUser() {
            return user;
        }

        public List<UserAccount> getUserAccounts() {
            return userAccounts;
        }

        public List<UserPhoto> getUserPhotos() {
            return userPhotos;
        }

        public List<UserSociotype> getUserSociotypes() {
            return userSociotypes;
        }

        public List<UserPurposeOfBeing> getUserPurposesOfBeing() {
            return userPurposesOfBeing;
        }

        public List<UserLocation> getUserLocations() {
            return userLocations;
        }
    }

}
