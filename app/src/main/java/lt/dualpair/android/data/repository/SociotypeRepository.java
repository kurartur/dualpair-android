package lt.dualpair.android.data.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import java.util.List;

import lt.dualpair.android.data.local.DualPairRoomDatabase;
import lt.dualpair.android.data.local.dao.SociotypeDao;
import lt.dualpair.android.data.local.entity.Sociotype;

public class SociotypeRepository {

    private final LiveData<List<Sociotype>> sociotypes;
    private DualPairRoomDatabase database;
    private SociotypeDao sociotypeDao;

    public SociotypeRepository(Application application) {
        database = DualPairRoomDatabase.getDatabase(application);
        sociotypeDao = database.sociotypeDao();
        sociotypes = sociotypeDao.getAllSociotypesLiveData();
    }

    public LiveData<List<Sociotype>> getSociotypes() {
        return sociotypes;
    }

    public LiveData<Sociotype> getSociotype(String code1) {
        return sociotypeDao.getSociotypeLive(code1);
    }

}
