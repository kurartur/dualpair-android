package lt.dualpair.android.data.local.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import lt.dualpair.android.data.local.entity.Sociotype;
import lt.dualpair.android.data.local.entity.SociotypeRelation;

@Dao
public abstract class SociotypeDao {

    @Query("SELECT * FROM sociotypes WHERE id = :id")
    public abstract Sociotype getSociotypeById(Long id);

    @Query("SELECT * FROM sociotypes WHERE id = :id")
    public abstract Flowable<Sociotype> getSociotype(Long id);

    @Query("SELECT * FROM sociotypes WHERE code = :code")
    public abstract Sociotype getSociotype(Sociotype.Code code);

    @Query("SELECT * FROM sociotypes")
    public abstract LiveData<List<Sociotype>> getAllSociotypesLiveData();

    @Query("SELECT * FROM sociotypes")
    public abstract List<Sociotype> getAllSociotypes();

    @Insert
    public abstract Long saveSociotype(Sociotype sociotype);

    @Insert
    public abstract void saveSociotypeRelation(SociotypeRelation sociotypeRelation);


}
