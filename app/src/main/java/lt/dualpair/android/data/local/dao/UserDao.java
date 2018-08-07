package lt.dualpair.android.data.local.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import lt.dualpair.android.data.local.entity.FullUserSociotype;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.data.local.entity.UserLocation;
import lt.dualpair.android.data.local.entity.UserPhoto;
import lt.dualpair.android.data.local.entity.UserPurposeOfBeing;
import lt.dualpair.android.data.local.entity.UserSearchParameters;
import lt.dualpair.android.data.local.entity.UserSociotype;

@Dao
public abstract class UserDao {

    @Query("SELECT * FROM users WHERE id = :id")
    public abstract Maybe<User> getUserMaybe(Long id);

    @Query("SELECT * FROM users WHERE id = :id")
    public abstract User getUser(Long id);

    @Query("SELECT * FROM user_sociotypes WHERE user_id = :userId")
    public abstract Maybe<List<UserSociotype>> getUserSociotypesMaybe(Long userId);

    @Query("SELECT * FROM user_sociotypes WHERE user_id = :userId")
    public abstract List<FullUserSociotype> getFullUserSociotypes(Long userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void saveUser(User user);

    @Insert
    public abstract void saveUserSociotypes(List<UserSociotype> sociotypeSet);

    @Query("SELECT * FROM user_sociotypes WHERE user_id = :userId")
    public abstract Flowable<List<UserSociotype>> getUserSociotypesFlowable(Long userId);

    @Transaction
    public void replaceUserSociotypes(Long userId, List<UserSociotype> userSociotypes) {
        deleteUserSociotypes(userId);
        saveUserSociotypes(userSociotypes);
    }

    @Query("DELETE FROM user_sociotypes WHERE user_id = :userId")
    protected abstract void deleteUserSociotypes(Long userId);

    @Insert
    public abstract void saveUserAccounts(List<UserAccount> userAccounts);

    @Transaction
    public void replaceUserAccounts(Long userId, List<UserAccount> userAccounts) {
        deleteUserAccounts(userId);
        saveUserAccounts(userAccounts);
    }

    @Query("DELETE FROM user_accounts WHERE user_id = :userId")
    public abstract void deleteUserAccounts(Long userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void saveUserPhotos(List<UserPhoto> userPhotos);

    @Transaction
    public void replaceUserPhotos(Long userId, List<UserPhoto> userPhotos) {
        deleteUserPhotos(userId);
        saveUserPhotos(userPhotos);
    }

    @Query("DELETE FROM user_photos where user_id = :userId")
    protected abstract void deleteUserPhotos(Long userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void saveUserSearchParameters(UserSearchParameters userSearchParameters);

    @Query("SELECT * FROM user_search_parameters WHERE user_id = :userId")
    public abstract Maybe<UserSearchParameters> getSearchParametersMaybe(Long userId);

    @Query("SELECT * FROM user_locations WHERE user_id = :userId ORDER BY id DESC LIMIT 1")
    public abstract UserLocation getLastLocation(Long userId);

    @Query("SELECT * FROM user_locations WHERE user_id = :userId ORDER BY id DESC LIMIT 1")
    public abstract LiveData<UserLocation> getLastLocationLiveData(Long userId);

    @Query("SELECT * FROM user_accounts WHERE user_id = :userId")
    public abstract List<UserAccount> getUserAccounts(Long userId);

    @Query("SELECT * FROM user_accounts WHERE user_id = :userId")
    public abstract LiveData<List<UserAccount>> getUserAccountsLive(Long userId);

    @Query("SELECT * FROM user_accounts WHERE user_id = :userId")
    public abstract Maybe<List<UserAccount>> getUserAccountsMaybe(Long userId);

    @Query("SELECT * FROM user_photos WHERE user_id = :userId ORDER BY position ASC")
    public abstract List<UserPhoto> getUserPhotos(Long userId);

    @Query("SELECT * FROM user_photos WHERE user_id = :userId ORDER BY position ASC")
    public abstract Flowable<List<UserPhoto>> getUserPhotosFlowable(Long userId);

    @Query("SELECT * FROM user_photos WHERE user_id = :userId")
    public abstract Maybe<List<UserPhoto>> getUserPhotosMaybe(Long userId);

    @Query("SELECT * FROM user_purposes_of_being WHERE user_id = :userId")
    public abstract Maybe<List<UserPurposeOfBeing>> getUserPurposesOfBeingMaybe(Long userId);

    @Insert
    public abstract void saveUserPurposesOfBeing(List<UserPurposeOfBeing> userPurposesOfBeing);

    @Query("SELECT * FROM user_purposes_of_being WHERE user_id = :userId")
    public abstract List<UserPurposeOfBeing> getUserPurposesOfBeing(Long userId);

    @Query("DELETE FROM user_purposes_of_being WHERE user_id = :userId")
    public abstract void deleteUserPurposesOfBeing(Long userId);

    @Transaction
    public void replaceUserPurposesOfBeing(Long userId, List<UserPurposeOfBeing> userPurposesOfBeing) {
        deleteUserPurposesOfBeing(userId);
        saveUserPurposesOfBeing(userPurposesOfBeing);
    }

    @Transaction
    public void replaceUserLocations(Long userId, List<UserLocation> userLocations) {
        deleteUserLocations(userId);
        saveUserLocations(userLocations);
    }

    @Insert
    protected abstract void saveUserLocations(List<UserLocation> userLocations);

    @Query("DELETE FROM user_locations WHERE user_id = :userId")
    protected abstract void deleteUserLocations(Long userId);

    @Insert
    public abstract void saveUserLocation(UserLocation userLocation);

}
