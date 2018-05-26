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
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.data.local.entity.UserLocation;
import lt.dualpair.android.data.local.entity.UserPhoto;
import lt.dualpair.android.data.local.entity.UserSearchParameters;
import lt.dualpair.android.data.local.entity.UserSociotype;

@Dao
public abstract class UserDao {

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    public abstract Flowable<User> getUserFlowable(Long id);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    public abstract Maybe<User> getUserMaybe(Long id);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    public abstract User getUser(Long id);

    @Query("SELECT * FROM user_sociotypes WHERE user_id = :userId")
    public abstract Flowable<List<UserSociotype>> getUserSociotypesFlowable(Long userId);

    @Query("SELECT * FROM user_sociotypes WHERE user_id = :userId")
    public abstract Maybe<List<UserSociotype>> getUserSociotypesMaybe(Long userId);

    @Query("SELECT * FROM user_sociotypes WHERE user_id = :userId")
    public abstract List<UserSociotype> getUserSociotypes(Long userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void saveUser(User user);

    @Insert
    public abstract void saveUserSociotypes(List<UserSociotype> sociotypeSet);

    @Insert
    public abstract void saveUserSociotype(UserSociotype sociotypeSet);

    @Insert
    public abstract void save(UserAccount userAccount);


    @Query("select * from users")
    public abstract LiveData<List<User>> getUsers();

    @Insert
    public abstract void saveUserAccounts(List<UserAccount> userAccounts);

    @Transaction
    public void replaceUserAccounts(Long userId, List<UserAccount> userAccounts) {
        deleteUserAccounts(userId);
        saveUserAccounts(userAccounts);
    }

    @Query("DELETE FROM user_accounts WHERE user_id = :userId")
    public abstract void deleteUserAccounts(Long userId);

    @Insert
    public abstract void saveUserPhotos(List<UserPhoto> userPhotos);

    @Transaction
    public void replaceUserPhotos(Long userId, List<UserPhoto> userPhotos) {
        deleteUserPhotos(userId);
        saveUserPhotos(userPhotos);
    }

    @Query("DELETE FROM user_photos where user_id = :userId")
    protected abstract void deleteUserPhotos(Long userId);

    @Insert
    public abstract void saveUserLocation(UserLocation userLocation);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void saveUserSearchParameters(UserSearchParameters userSearchParameters);

    @Query("SELECT * FROM user_search_parameters WHERE user_id = :userId")
    public abstract Maybe<UserSearchParameters> getSearchParametersMaybe(Long userId);

    @Query("SELECT * FROM user_locations WHERE user_id = :userId ORDER BY id DESC LIMIT 1")
    public abstract LiveData<UserLocation> getLastLocation(Long userId);
}
