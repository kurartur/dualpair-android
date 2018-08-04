package lt.dualpair.android.data.local;

import lt.dualpair.android.data.local.dao.SociotypeDao;
import lt.dualpair.android.data.local.entity.Sociotype;
import lt.dualpair.android.data.local.entity.SociotypeRelation;

public class DatabasePopulator {

    private DualPairRoomDatabase database;
    private SociotypeDao sociotypeDao;

    public DatabasePopulator(DualPairRoomDatabase database) {
        this.database = database;
        this.sociotypeDao = database.sociotypeDao();
    }

    public void populate() {
        database.runInTransaction(() -> {
            addSociotypesAndRelation(Sociotype.Code.LSE, Sociotype.Code.EII);
            addSociotypesAndRelation(Sociotype.Code.LIE, Sociotype.Code.ESI);
            addSociotypesAndRelation(Sociotype.Code.ESE, Sociotype.Code.LII);
            addSociotypesAndRelation(Sociotype.Code.EIE, Sociotype.Code.LSI);
            addSociotypesAndRelation(Sociotype.Code.SLE, Sociotype.Code.IEI);
            addSociotypesAndRelation(Sociotype.Code.SEE, Sociotype.Code.ILI);
            addSociotypesAndRelation(Sociotype.Code.ILE, Sociotype.Code.SEI);
            addSociotypesAndRelation(Sociotype.Code.IEE, Sociotype.Code.SLI);
        });
    }

    private void addSociotypesAndRelation(Sociotype.Code code1, Sociotype.Code code2) {
        Long id1 = sociotypeDao.saveSociotype(new Sociotype(code1));
        Long id2 = sociotypeDao.saveSociotype(new Sociotype(code2));
        sociotypeDao.saveSociotypeRelation(new SociotypeRelation(id1, id2));
        sociotypeDao.saveSociotypeRelation(new SociotypeRelation(id2, id1));
    }

}
