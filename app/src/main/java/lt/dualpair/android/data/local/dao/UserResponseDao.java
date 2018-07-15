package lt.dualpair.android.data.local.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import lt.dualpair.android.data.local.entity.UserResponse;

@Dao
public abstract class UserResponseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void save(UserResponse userResponse);

    @Query("SELECT * FROM user_responses ORDER BY date DESC")
    public abstract Flowable<List<UserResponse>> getResponsesFlowable();

    @Query("SELECT * FROM user_responses WHERE user_id = :userId")
    public abstract UserResponse getResponse(Long userId);
}
