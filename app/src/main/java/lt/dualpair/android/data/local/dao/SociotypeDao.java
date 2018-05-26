package lt.dualpair.android.data.local.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import lt.dualpair.android.data.local.entity.Sociotype;

@Dao
public abstract class SociotypeDao {

    @Query("SELECT * FROM sociotypes WHERE id = :id")
    public abstract Sociotype getSociotypeById(Long id);

    @Query("SELECT * FROM sociotypes WHERE code1 = :code1")
    public abstract Sociotype getSociotype(String code1);

    @Query("SELECT * FROM sociotypes WHERE code1 = :code1")
    public abstract LiveData<Sociotype> getSociotypeLive(String code1);

    @Query("SELECT * FROM sociotypes")
    public abstract List<Sociotype> getAllSociotypes();

    @Query("SELECT * FROM sociotypes")
    public abstract LiveData<List<Sociotype>> getAllSociotypesLiveData();

    @Insert
    public abstract void saveSociotypes(List<Sociotype> sociotypes);

    @Insert
    public abstract void saveSociotypes(Sociotype[] sociotypes);

}
