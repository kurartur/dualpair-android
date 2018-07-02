package lt.dualpair.android.data.local.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import lt.dualpair.android.data.local.entity.Swipe;

@Dao
public abstract class SwipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void save(Swipe swipe);

    @Query("SELECT * FROM swipes ORDER BY id DESC")
    public abstract Flowable<List<Swipe>> getSwipesFlowable();

    @Query("SELECT * FROM swipes WHERE id = :id")
    public abstract Swipe getSwipe(Long id);

    @Query("SELECT * FROM swipes WHERE who = :opponentId")
    public abstract Swipe getSwipeByOpponent(Long opponentId);
}
