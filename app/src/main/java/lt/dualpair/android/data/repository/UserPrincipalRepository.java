package lt.dualpair.android.data.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.local.DualPairRoomDatabase;
import lt.dualpair.android.data.local.dao.SociotypeDao;
import lt.dualpair.android.data.local.dao.UserDao;
import lt.dualpair.android.data.local.entity.FullUserSociotype;
import lt.dualpair.android.data.local.entity.PurposeOfBeing;
import lt.dualpair.android.data.local.entity.RelationshipStatus;
import lt.dualpair.android.data.local.entity.Sociotype;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.data.local.entity.UserLocation;
import lt.dualpair.android.data.local.entity.UserPhoto;
import lt.dualpair.android.data.local.entity.UserPurposeOfBeing;
import lt.dualpair.android.data.local.entity.UserSearchParameters;
import lt.dualpair.android.data.local.entity.UserSociotype;
import lt.dualpair.android.data.mapper.UserResourceMapper;
import lt.dualpair.android.data.remote.client.authentication.LogoutClient;
import lt.dualpair.android.data.remote.client.user.GetAvailablePhotosClient;
import lt.dualpair.android.data.remote.client.user.GetSearchParametersClient;
import lt.dualpair.android.data.remote.client.user.GetUserPrincipalClient;
import lt.dualpair.android.data.remote.client.user.SetLocationClient;
import lt.dualpair.android.data.remote.client.user.SetPhotosClient;
import lt.dualpair.android.data.remote.client.user.SetSearchParametersClient;
import lt.dualpair.android.data.remote.client.user.SetUserSociotypesClient;
import lt.dualpair.android.data.remote.client.user.UpdateUserClient;
import lt.dualpair.android.data.resource.Location;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.resource.SearchParameters;
import lt.dualpair.android.ui.accounts.AccountType;

public class UserPrincipalRepository {

    private static final String TAG = UserPrincipalRepository.class.getName();

    private UserDao userDao;
    private SociotypeDao sociotypeDao;
    private Long userId;
    private DualPairRoomDatabase database;

    private static long lastPrincipalApiRequest;
    private static final long INTERVAL = 1000 * 60 * 1; // one request per minute

    public UserPrincipalRepository(Application application) {
        database = DualPairRoomDatabase.getDatabase(application);
        userDao = database.userDao();
        sociotypeDao = database.sociotypeDao();
        userId = AccountUtils.getUserId(application);
    }

    public Single<User> getUser() {
        Maybe<User> localUser = userDao.getUserMaybe(userId);
        Single<User> remoteUser = userPrincipalFromApiObservable()
                .map(user -> new UserResourceMapper(sociotypeDao).map(user).getUser()).singleOrError();
        return Maybe.concat(localUser, remoteUser.toMaybe()).firstElement().toSingle();
    }

    public Single<List<UserSociotype>> getSociotypes() {
        Maybe<List<UserSociotype>> local = userDao.getUserSociotypesMaybe(userId)
                .filter(list -> !list.isEmpty());
        Single<List<UserSociotype>> remote = new GetUserPrincipalClient().observable()
                .subscribeOn(Schedulers.io())
                .map(user -> new UserResourceMapper(sociotypeDao).map(user).getUserSociotypes()).singleOrError();
        return Maybe.concat(local, remote.toMaybe()).firstElement().toSingle();
    }

    private UserResourceMapper.Result saveUserResource(lt.dualpair.android.data.resource.User userResource) {
        UserResourceMapper.Result mappingResult = new UserResourceMapper(sociotypeDao).map(userResource);
        database.runInTransaction(() -> {
            userDao.saveUser(mappingResult.getUser());
            userDao.replaceUserAccounts(userId, mappingResult.getUserAccounts());
            userDao.replaceUserPhotos(userId, mappingResult.getUserPhotos());
            userDao.replaceUserSociotypes(userId, mappingResult.getUserSociotypes());
            userDao.replaceUserPurposesOfBeing(userId, mappingResult.getUserPurposesOfBeing());
            userDao.replaceUserLocations(userId, mappingResult.getUserLocations());
        });
        return mappingResult;
    }

    public Completable setSociotype(String sociotypeCode) {
        Set<String> sociotypes = new HashSet<>();
        sociotypes.add(sociotypeCode);
        return new SetUserSociotypesClient(userId, sociotypes).completable()
                .doOnComplete(() -> {
                    Sociotype sociotype = sociotypeDao.getSociotype(sociotypeCode);
                    UserSociotype userSociotype = new UserSociotype();
                    userSociotype.setUserId(userId);
                    userSociotype.setSociotypeId(sociotype.getId());
                    userDao.replaceUserSociotypes(userId, Arrays.asList(userSociotype));
                });
    }

    public Completable saveLocation(android.location.Location location) {
        Location locationResource = new Location();
        locationResource.setLatitude(location.getLatitude());
        locationResource.setLongitude(location.getLongitude());
        return new SetLocationClient(userId, locationResource).completable()
                .doOnComplete(new Action() {
                    @Override
                    public void run() {
                        userDao.saveUserLocation(UserLocation.fromAndroidLocation(location, userId));
                    }
                });
    }

    public Single<UserSearchParameters> getSearchParameters() {
        Maybe<UserSearchParameters> local = userDao.getSearchParametersMaybe(userId);
        Single<UserSearchParameters> remote = new GetSearchParametersClient(userId).observable()
                .map(new Function<SearchParameters, UserSearchParameters>() {
                    @Override
                    public UserSearchParameters apply(SearchParameters searchParametersResource) {
                        UserSearchParameters userSearchParameters = new UserSearchParameters();
                        userSearchParameters.setUserId(userId);
                        userSearchParameters.setSearchFemale(searchParametersResource.getSearchFemale());
                        userSearchParameters.setSearchMale(searchParametersResource.getSearchMale());
                        userSearchParameters.setMinAge(searchParametersResource.getMinAge());
                        userSearchParameters.setMaxAge(searchParametersResource.getMaxAge());
                        userDao.saveUserSearchParameters(userSearchParameters);
                        return userSearchParameters;
                    }
                }).singleOrError();
        return Maybe.concat(local, remote.toMaybe()).firstElement().toSingle();
    }

    public Completable logout() {
        return new LogoutClient().completable();
    }

    public Completable setSearchParameters(UserSearchParameters sp) {
        SearchParameters spResource = new SearchParameters();
        spResource.setMinAge(sp.getMinAge());
        spResource.setMaxAge(sp.getMaxAge());
        spResource.setSearchFemale(sp.getSearchFemale());
        spResource.setSearchMale(sp.getSearchMale());
        return new SetSearchParametersClient(userId, spResource).completable()
                .doOnComplete(new Action() {
                    @Override
                    public void run() {
                        sp.setUserId(userId);
                        userDao.saveUserSearchParameters(sp);
                    }
                });
    }

    public Completable updateUser(String name, Date dateOfBirth, String description, RelationshipStatus relationshipStatus, List<PurposeOfBeing> purposesOfBeing) {
        lt.dualpair.android.data.resource.User userResource = new lt.dualpair.android.data.resource.User();
        userResource.setId(userId);
        userResource.setName(name);
        userResource.setDateOfBirth(dateOfBirth);
        userResource.setDescription(description);
        userResource.setRelationshipStatus(relationshipStatus.getCode());
        userResource.setPurposesOfBeing(extractCodes(purposesOfBeing));
        return new UpdateUserClient(userResource).completable()
                .doOnComplete(() -> {
                    database.runInTransaction(new Runnable() {
                        @Override
                        public void run() {
                            User user = userDao.getUser(userId);
                            user.setName(name);
                            user.setDateOfBirth(dateOfBirth);
                            user.setDescription(description);
                            user.setRelationshipStatus(relationshipStatus);
                            userDao.saveUser(user);

                            List<UserPurposeOfBeing> userPurposesOfBeing = new ArrayList<>();
                            for (PurposeOfBeing purposeOfBeing : purposesOfBeing) {
                                UserPurposeOfBeing userPurposeOfBeing = new UserPurposeOfBeing();
                                userPurposeOfBeing.setUserId(userId);
                                userPurposeOfBeing.setPurpose(purposeOfBeing);
                                userPurposesOfBeing.add(userPurposeOfBeing);
                                userDao.replaceUserPurposesOfBeing(userId, userPurposesOfBeing);
                            }
                        }
                    });
                });
    }

    private Set<String> extractCodes(List<PurposeOfBeing> purposesOfBeing) {
        Set<String> codes = new HashSet<>();
        for (PurposeOfBeing purposeOfBeing : purposesOfBeing) {
            codes.add(purposeOfBeing.getCode());
        }
        return codes;
    }

    public Single<List<UserPhoto>> getPhotos() {
        Maybe<List<UserPhoto>> local = userDao.getUserPhotosMaybe(userId);
        Single<List<UserPhoto>> remote = userPrincipalFromApiObservable()
                .map(user -> new UserResourceMapper(sociotypeDao).map(user).getUserPhotos()).singleOrError();
        return Maybe.concat(local, remote.toMaybe()).firstElement().toSingle();
    }

    public Completable savePhotos(List<UserPhoto> photos) {
        List<Photo> photoResources = new ArrayList<>();
        for (UserPhoto userPhoto : photos) {
            Photo photoResource = new Photo();
            photoResource.setAccountType(AccountType.valueOf(userPhoto.getAccountType()));
            photoResource.setIdOnAccount(userPhoto.getIdOnAccount());
            photoResource.setPosition(userPhoto.getPosition());
            photoResource.setSourceUrl(userPhoto.getSourceLink());
            photoResources.add(photoResource);
        }
        return new SetPhotosClient(userId, photoResources).completable()
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        userDao.replaceUserPhotos(userId, photos);
                    }
                });
    }

    public Single<List<UserAccount>> getUserAccounts() {
        Maybe<List<UserAccount>> local = userDao.getUserAccountsMaybe(userId);
        Single<List<UserAccount>> remote = userPrincipalFromApiObservable()
                .map(user -> new UserResourceMapper(sociotypeDao).map(user).getUserAccounts()).singleOrError();
        return Maybe.concat(local, remote.toMaybe()).firstElement().toSingle();
    }

    public Single<List<UserPurposeOfBeing>> getUserPurposesOfBeing() {
        Maybe<List<UserPurposeOfBeing>> local = userDao.getUserPurposesOfBeingMaybe(userId);
        Single<List<UserPurposeOfBeing>> remote = userPrincipalFromApiObservable()
                .map(user -> new UserResourceMapper(sociotypeDao).map(user).getUserPurposesOfBeing()).singleOrError();
        return Maybe.concat(local, remote.toMaybe()).firstElement().toSingle();
    }

    public LiveData<UserLocation> getLastStoredLocation() {
        return userDao.getLastLocation(userId);
    }

    private Observable<lt.dualpair.android.data.resource.User> userPrincipalFromApiObservable() {
        return new GetUserPrincipalClient().observable()
                .subscribeOn(Schedulers.io())
                .doOnNext(userResource -> saveUserResource(userResource))
                .doOnComplete(() -> lastPrincipalApiRequest = System.currentTimeMillis());
    }

    public Completable loadFromApi() {
        return userPrincipalFromApiObservable().ignoreElements();
    }

    public Completable loadFromApiIfTime() {
        if (System.currentTimeMillis() - lastPrincipalApiRequest > INTERVAL) {
            return loadFromApi();
        } else {
            return Completable.complete();
        }
    }

    public LiveData<User> getUserLive() {
        return userDao.getUserLive(userId);
    }

    public LiveData<List<FullUserSociotype>> getFullUserSociotypesLive() {
        return userDao.getFullUserSociotypesLive(userId);
    }

    public LiveData<List<UserAccount>> getUserAccountsLive() {
        return userDao.getUserAccountsLive(userId);
    }

    public LiveData<List<UserPhoto>> getUserPhotosLive() {
        return userDao.getUserPhotosLive(userId);
    }

    public LiveData<List<UserPurposeOfBeing>> getUserPurposesOfBeingLive() {
        return userDao.getUserPurposesOfBeingLive(userId);
    }

    public Observable<List<UserPhoto>> getAvailableUserPhotos(AccountType accountType) {
        return new GetAvailablePhotosClient(userId, accountType).observable()
                .map(photos -> {
                    List<UserPhoto> userPhotos = new ArrayList<>();
                    for(Photo photoResource : photos) {
                        UserPhoto userPhoto = new UserPhoto();
                        userPhoto.setUserId(userId);
                        userPhoto.setAccountType(photoResource.getAccountType().toString());
                        userPhoto.setIdOnAccount(photoResource.getIdOnAccount());
                        userPhoto.setSourceLink(photoResource.getSourceUrl());
                        userPhotos.add(userPhoto);
                    }
                    return userPhotos;
                });
    }
}
