package lt.dualpair.android.data.local.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import io.reactivex.Flowable;
import lt.dualpair.android.data.local.entity.History;
import lt.dualpair.android.data.local.entity.Swipe;

@Dao
public abstract class SwipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void save(Swipe swipe);

    @Query("select sw.id, sw.who as user_id, up.source_link as sourceLink, u.name, sw.type as answer " +
            "from swipes sw " +
            "inner join users u on u.id = sw.who " +
            "inner join (select max(id) as id, user_id from user_photos where position = 0 group by user_id) upid on upid.user_id = sw.who " +
            "inner join user_photos up on up.id = upid.id " +
            "order by sw.id desc")
    @Transaction
    public abstract LiveData<List<History>> getHistory();

    @Query("SELECT * FROM swipes")
    public abstract Flowable<List<Swipe>> getSwipesFlowable();

    @Query("SELECT * FROM swipes WHERE id = :id")
    public abstract Swipe getSwipe(Long id);

    @Query("SELECT * FROM swipes WHERE who = :opponentId")
    public abstract Swipe getSwipeByOpponent(Long opponentId);
}
