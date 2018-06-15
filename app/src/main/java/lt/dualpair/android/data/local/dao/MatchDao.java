package lt.dualpair.android.data.local.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import lt.dualpair.android.data.local.entity.Match;

@Dao
public interface MatchDao {

    @Query("SELECT * FROM matches ORDER BY date DESC")
    Flowable<List<Match>> getMatchesFlowable();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveMatch(Match match);

    @Query("DELETE FROM matches")
    void deleteAll();

    @Query("DELETE from matches WHERE id NOT IN (:ids)")
    void deleteNotIn(String ids);

    @Query("SELECT * FROM matches WHERE id = :matchId")
    Match getMatch(Long matchId);

    @Query("SELECT * FROM matches WHERE opponent_id = :opponentId")
    Match getMatchByOpponent(Long opponentId);

}
